import { requireNonNull } from '../../shared/requireNonNull.js';
import { RolloutDecision } from './RolloutDecision.js';
import type { RolloutSnapshot } from './RolloutSnapshot.js';
import type { RolloutThresholds } from './RolloutThresholds.js';

export class RolloutPolicy {
  private readonly thresholds: RolloutThresholds;

  constructor(thresholds: RolloutThresholds) {
    this.thresholds = requireNonNull(thresholds, 'thresholds');
  }

  decide(snapshot: RolloutSnapshot): RolloutDecision {
    requireNonNull(snapshot, 'snapshot');

    if (
      snapshot.mismatchedResponses > 0 ||
      snapshot.errorRate() > this.thresholds.maximumErrorRate ||
      snapshot.p95LatencyMillis > this.thresholds.maximumP95LatencyMillis
    ) {
      return RolloutDecision.ROLLBACK;
    }
    if (snapshot.sampleSize < this.thresholds.minimumSampleSize) {
      return RolloutDecision.HOLD;
    }
    return RolloutDecision.ADVANCE;
  }
}
