import { requireNonNull } from '../../shared/requireNonNull.js';
import type { ChangeSet } from './ChangeSet.js';
import { ReadinessProblem } from './ReadinessProblem.js';
import { ReviewReadiness } from './ReviewReadiness.js';

/**
 * An example team policy, not a universal definition of review readiness.
 */
export class ExampleTeamReviewPolicy {
  assess(changeSet: ChangeSet): ReviewReadiness {
    requireNonNull(changeSet, 'changeSet');

    const problems: ReadinessProblem[] = [];
    if (changeSet.intents.size === 0) {
      problems.push(ReadinessProblem.MISSING_INTENT);
    } else if (changeSet.intents.size > 1) {
      problems.push(ReadinessProblem.MIXED_PRIMARY_INTENTS);
    }
    if (changeSet.verificationEvidence.length === 0) {
      problems.push(ReadinessProblem.MISSING_VERIFICATION_EVIDENCE);
    }
    if (!changeSet.independentlyGreenBuild) {
      problems.push(ReadinessProblem.BUILD_NOT_INDEPENDENTLY_GREEN);
    }
    return new ReviewReadiness(problems);
  }
}
