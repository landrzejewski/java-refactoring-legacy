import { describe, expect, it } from 'vitest';
import { ReleaseGate } from '../../src/module7/doublenegative/after/ReleaseGate.js';
import { ReleaseReadiness } from '../../src/module7/doublenegative/after/ReleaseReadiness.js';
import { LegacyReleaseGate } from '../../src/module7/doublenegative/before/LegacyReleaseGate.js';
import { LegacyReleaseReadiness } from '../../src/module7/doublenegative/before/LegacyReleaseReadiness.js';

const NULL = null as never;

function failureOf(action: () => unknown): Error {
  try {
    action();
  } catch (error) {
    return error as Error;
  }
  throw new Error('Expected action to throw');
}

describe('DoubleNegativeEquivalenceTest', () => {
  it('positiveNamesPreserveAllEightTruthTableRows', () => {
    const before = new LegacyReleaseGate();
    const after = new ReleaseGate();
    const values = [false, true];

    for (const notApproved of values) {
      for (const testsNotPassed of values) {
        for (const windowNotOpen of values) {
          const legacyResult = before.canRelease(
            new LegacyReleaseReadiness(
              notApproved,
              testsNotPassed,
              windowNotOpen));
          const refactoredResult = after.canRelease(
            new ReleaseReadiness(
              !notApproved,
              !testsNotPassed,
              !windowNotOpen));

          expect(refactoredResult).toBe(legacyResult);
          expect(refactoredResult)
            .toBe(!notApproved && !testsNotPassed && !windowNotOpen);
        }
      }
    }
  });

  it('bothGatesRejectMissingReadinessWithTheSameContract', () => {
    const legacyFailure = failureOf(() => new LegacyReleaseGate().canRelease(NULL));
    const refactoredFailure = failureOf(() => new ReleaseGate().canRelease(NULL));

    expect(refactoredFailure.constructor).toBe(legacyFailure.constructor);
    expect(refactoredFailure.message).toBe(legacyFailure.message);
  });
});
