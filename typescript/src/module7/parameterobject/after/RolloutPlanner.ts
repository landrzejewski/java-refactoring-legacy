import { requireNonNull } from '../../../shared/requireNonNull.js';
import { addExact, ceilDiv, multiplyExact } from '../../ExactMath.js';
import type { RolloutSpec } from './RolloutSpec.js';

export class RolloutPlanner {
  private static readonly DEPLOYMENT_SECONDS_PER_BATCH = 30;

  estimateSeconds(spec: RolloutSpec): number {
    requireNonNull(spec, 'spec');

    const batches = ceilDiv(
      spec.instances,
      spec.batchSize);
    const deploymentSeconds = multiplyExact(
      batches,
      RolloutPlanner.DEPLOYMENT_SECONDS_PER_BATCH);
    const pauseTime = multiplyExact(
      batches - 1,
      spec.pauseSeconds);
    return addExact(deploymentSeconds, pauseTime);
  }

  describe(spec: RolloutSpec): string {
    requireNonNull(spec, 'spec');

    return 'service=' + spec.service
      + ';region=' + spec.region
      + ';instances=' + spec.instances
      + ';batchSize=' + spec.batchSize
      + ';pauseSeconds=' + spec.pauseSeconds;
  }
}
