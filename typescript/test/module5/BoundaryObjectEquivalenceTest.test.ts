import { describe, expect, it } from 'vitest';
import { IllegalArgumentError } from '../../src/shared/errors.js';
import type { LegacyCiClient } from '../../src/module5/boundaryobject/LegacyCiClient.js';
import { BuildReport, Status } from '../../src/module5/boundaryobject/after/BuildReport.js';
import { CiBuildReportsAdapter } from '../../src/module5/boundaryobject/after/CiBuildReportsAdapter.js';
import { ReleaseGate } from '../../src/module5/boundaryobject/after/ReleaseGate.js';
import { LegacyReleaseGate } from '../../src/module5/boundaryobject/before/LegacyReleaseGate.js';
import { NumberFormatError } from '../../src/module5/support.js';

function payload(id: string, status: string, coverage: string, failed: string, commit: string): ReadonlyMap<string, string> {
  return new Map([
    ['id', id],
    ['status', status],
    ['coverage', coverage],
    ['failed', failed],
    ['commit', commit],
  ]);
}

const BUILDS: ReadonlyMap<string, ReadonlyMap<string, string>> = new Map([
  ['build-1', payload('build-1', 'SUCCESS', '82.5', '0', 'A1B2C3D')],
  ['build-2', payload('build-2', ' success ', '80', '0', 'a1b2c3d4e5')],
  ['build-3', payload('build-3', 'SUCCESS', '79.9', '0', 'abcdef0')],
  ['build-4', payload('build-4', 'FAILED', '95', '3', 'abcdef1')],
  ['build-5', payload('build-5', 'running', '0', '0', 'abcdef2')],
]);

// Odpowiednik BUILDS::get.
const ci: LegacyCiClient = { fetchBuild: buildId => BUILDS.get(buildId) };

function captureFailure(action: () => unknown): Error {
  try {
    action();
  } catch (error) {
    return error as Error;
  }
  throw new Error('expected failure');
}

describe('BoundaryObjectEquivalenceTest', () => {
  it('domainDecisionsAreTheSameForEveryValidPayload', () => {
    const before = new LegacyReleaseGate(ci);
    const after = new ReleaseGate(new CiBuildReportsAdapter(ci));

    for (const buildId of BUILDS.keys()) {
      expect(after.canRelease(buildId), buildId).toBe(before.canRelease(buildId));
      expect(after.summary(buildId), buildId).toBe(before.summary(buildId));
    }
    expect(after.summary('build-1')).toBe('build-1 [a1b2c3d] SUCCESS coverage=82.5% failed=0');
    expect([...BUILDS.keys()].sort().map(buildId => after.canRelease(buildId))).toEqual([
      true, true, false, false, false,
    ]);
  });

  it.each(['status', 'coverage', 'failed', 'commit', 'id'])(
    'payloadWithMissingFieldIsRejectedAtTheBoundary(%s)',
    missing => {
      const broken = new Map(BUILDS.get('build-1'));
      broken.delete(missing);
      const adapter = new CiBuildReportsAdapter({ fetchBuild: () => broken });

      const failure = captureFailure(() => adapter.find('build-1'));

      expect(failure).toBeInstanceOf(IllegalArgumentError);
      expect(failure.message).toBe('invalid CI payload: missing ' + missing);
    },
  );

  it('malformedValuesAreRejectedAtTheBoundaryInsteadOfInsideTheDomain', () => {
    const adapter = new CiBuildReportsAdapter({
      fetchBuild: () => payload('build-9', 'SUCCESS', 'eighty', '0', 'abcdef0'),
    });
    const legacy = new LegacyReleaseGate({
      fetchBuild: () => payload('build-9', 'SUCCESS', 'eighty', '0', 'abcdef0'),
    });

    expect(() => legacy.canRelease('build-9')).toThrow(NumberFormatError);
    const malformed = captureFailure(() => adapter.find('build-9'));
    expect(malformed).toBeInstanceOf(IllegalArgumentError);
    expect(malformed.message).toBe('invalid CI payload: For input string: "eighty"');
    const outOfRange = captureFailure(() =>
      new CiBuildReportsAdapter({ fetchBuild: () => payload('b', 'SUCCESS', '120', '0', 'abcdef0') }).find('b'));
    expect(outOfRange).toBeInstanceOf(IllegalArgumentError);
    expect(outOfRange.message).toBe('invalid CI payload: coverage must be between 0 and 100');
  });

  it('boundaryObjectValidatesItselfIndependentlyOfThePayload', () => {
    expect(() => new BuildReport('b', Status.SUCCESS, 50, -1, 'abcdef0')).toThrow(IllegalArgumentError);
    expect(() => new BuildReport('b', Status.SUCCESS, 50, 0, 'not-a-sha')).toThrow(IllegalArgumentError);
  });
});
