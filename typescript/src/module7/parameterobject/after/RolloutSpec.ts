import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';

export class RolloutSpec {
  constructor(
    readonly service: string,
    readonly region: string,
    readonly instances: number,
    readonly batchSize: number,
    readonly pauseSeconds: number,
  ) {
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
