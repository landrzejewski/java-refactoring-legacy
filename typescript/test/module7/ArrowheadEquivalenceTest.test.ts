import { describe, expect, it } from 'vitest';
import { DeploymentCandidate } from '../../src/module7/arrowhead/DeploymentCandidate.js';
import { Eligibility } from '../../src/module7/arrowhead/Eligibility.js';
import { DeploymentEligibility } from '../../src/module7/arrowhead/after/DeploymentEligibility.js';
import { LegacyDeploymentEligibility } from '../../src/module7/arrowhead/before/LegacyDeploymentEligibility.js';

describe('ArrowheadEquivalenceTest', () => {
  const before = new LegacyDeploymentEligibility();
  const after = new DeploymentEligibility();

  function assertSameDecision(
    candidate: DeploymentCandidate | null,
    expected: Eligibility,
  ): void {
    expect(before.evaluate(candidate)).toBe(expected);
    expect(after.evaluate(candidate)).toBe(expected);
    expect(after.evaluate(candidate)).toBe(before.evaluate(candidate));
  }

  it('guardClausesPreserveTheCompleteDecisionMatrix', () => {
    const values = [false, true];

    for (const approved of values) {
      for (const testsPassed of values) {
        for (const windowOpen of values) {
          const candidate = new DeploymentCandidate(
            'rel-42', approved, testsPassed, windowOpen);
          const expected = !approved
            ? Eligibility.NOT_APPROVED
            : !testsPassed
              ? Eligibility.TESTS_FAILED
              : !windowOpen
                ? Eligibility.WINDOW_CLOSED
                : Eligibility.ELIGIBLE;

          expect(before.evaluate(candidate)).toBe(expected);
          expect(after.evaluate(candidate)).toBe(expected);
        }
      }
    }
  });

  it('guardClausesPreserveValidationAndFailurePriority', () => {
    assertSameDecision(null, Eligibility.MISSING_CANDIDATE);
    assertSameDecision(
      new DeploymentCandidate(null, false, false, false),
      Eligibility.INVALID_RELEASE_ID);
    assertSameDecision(
      new DeploymentCandidate(' \t', false, false, false),
      Eligibility.INVALID_RELEASE_ID);
    assertSameDecision(
      new DeploymentCandidate('rel-42', false, false, false),
      Eligibility.NOT_APPROVED);
    assertSameDecision(
      new DeploymentCandidate('rel-42', true, false, false),
      Eligibility.TESTS_FAILED);
    assertSameDecision(
      new DeploymentCandidate('rel-42', true, true, false),
      Eligibility.WINDOW_CLOSED);
  });
});
