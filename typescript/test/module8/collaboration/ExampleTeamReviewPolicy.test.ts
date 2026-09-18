import { describe, expect, it } from 'vitest';
import { UnsupportedOperationError } from '../../../src/shared/errors.js';
import { ChangeIntent } from '../../../src/module8/collaboration/ChangeIntent.js';
import { ChangeSet } from '../../../src/module8/collaboration/ChangeSet.js';
import { EvidenceKind } from '../../../src/module8/collaboration/EvidenceKind.js';
import { ExampleTeamReviewPolicy } from '../../../src/module8/collaboration/ExampleTeamReviewPolicy.js';
import { ReadinessProblem } from '../../../src/module8/collaboration/ReadinessProblem.js';
import { VerificationEvidence } from '../../../src/module8/collaboration/VerificationEvidence.js';

describe('ExampleTeamReviewPolicyTest', () => {
  const policy = new ExampleTeamReviewPolicy();

  it('acceptsAFocusedChangeWithEvidenceAndAGreenBuild', () => {
    const changeSet = new ChangeSet(
      'Extract deployment clock',
      new Set([ChangeIntent.REFACTORING]),
      [new VerificationEvidence(EvidenceKind.AUTOMATED_TEST, 'npm test: 42 tests passed')],
      true,
    );

    const readiness = policy.assess(changeSet);

    expect(readiness.ready()).toBe(true);
    expect(readiness.problems).toEqual([]);
  });

  it('reportsMixedIntentMissingEvidenceAndNonGreenBuildInStableOrder', () => {
    const changeSet = new ChangeSet(
      'Move validator and change its rules',
      new Set([ChangeIntent.REFACTORING, ChangeIntent.BEHAVIOR_CHANGE]),
      [],
      false,
    );

    const readiness = policy.assess(changeSet);

    expect(readiness.ready()).toBe(false);
    expect(readiness.problems).toEqual([
      ReadinessProblem.MIXED_PRIMARY_INTENTS,
      ReadinessProblem.MISSING_VERIFICATION_EVIDENCE,
      ReadinessProblem.BUILD_NOT_INDEPENDENTLY_GREEN,
    ]);
  });

  it('reportsAnUnspecifiedIntentAsATypedProblem', () => {
    const changeSet = new ChangeSet(
      'Unclassified change',
      new Set<ChangeIntent>(),
      [new VerificationEvidence(EvidenceKind.STATIC_ANALYSIS, 'No new findings')],
      true,
    );

    expect(policy.assess(changeSet).problems).toEqual([ReadinessProblem.MISSING_INTENT]);
  });

  it('snapshotsMutableInputCollections', () => {
    const intents = new Set([ChangeIntent.CHARACTERIZATION_TESTS]);
    const evidence = [
      new VerificationEvidence(EvidenceKind.AUTOMATED_TEST, 'Characterization suite passed'),
    ];

    const changeSet = new ChangeSet('Capture behavior', intents, evidence, true);
    intents.add(ChangeIntent.ROLLOUT);
    evidence.length = 0;

    expect(new Set(changeSet.intents)).toEqual(new Set([ChangeIntent.CHARACTERIZATION_TESTS]));
    expect(changeSet.verificationEvidence.length).toBe(1);
    expect(() => (changeSet.intents as Set<ChangeIntent>).add(ChangeIntent.ROLLOUT)).toThrow(
      UnsupportedOperationError,
    );
    expect(() => (changeSet.verificationEvidence as VerificationEvidence[]).splice(0)).toThrow(
      TypeError,
    );
  });
});
