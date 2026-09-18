import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { addExact, ceilDiv, multiplyExact } from '../../ExactMath.js';

export class LegacyRolloutPlanner {
  private static readonly DEPLOYMENT_SECONDS_PER_BATCH = 30;

  estimateSeconds(
    service: string,
    region: string,
    instances: number,
    batchSize: number,
    pauseSeconds: number,
  ): number {
    this.validate(service, region, instances, batchSize, pauseSeconds);

    const batches = ceilDiv(instances, batchSize);
    const deploymentSeconds = multiplyExact(
      batches,
      LegacyRolloutPlanner.DEPLOYMENT_SECONDS_PER_BATCH);
    const pauseTime = multiplyExact(
      batches - 1,
      pauseSeconds);
    return addExact(deploymentSeconds, pauseTime);
  }

  describe(
    service: string,
    region: string,
    instances: number,
    batchSize: number,
    pauseSeconds: number,
  ): string {
    this.validate(service, region, instances, batchSize, pauseSeconds);

    return 'service=' + service
      + ';region=' + region
      + ';instances=' + instances
      + ';batchSize=' + batchSize
      + ';pauseSeconds=' + pauseSeconds;
  }

  private validate(
    service: string,
    region: string,
    instances: number,
    batchSize: number,
    pauseSeconds: number,
  ): void {
    requireNonNull(service, 'service');
    if (service.trim() === '') {
      throw new IllegalArgumentError('service must not be blank');
    }
    requireNonNull(region, 'region');
    if (region.trim() === '') {
      throw new IllegalArgumentError('region must not be blank');
    }
    if (instances <= 0) {
      throw new IllegalArgumentError(
        'instances must be greater than zero');
    }
    if (batchSize <= 0) {
      throw new IllegalArgumentError(
        'batchSize must be greater than zero');
    }
    if (pauseSeconds < 0) {
      throw new IllegalArgumentError(
        'pauseSeconds must not be negative');
    }
  }
}
