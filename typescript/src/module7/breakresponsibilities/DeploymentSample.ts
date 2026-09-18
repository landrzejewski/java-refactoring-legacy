import { IllegalArgumentError } from '../../shared/errors.js';

export class DeploymentSample {
  constructor(
    readonly leadTimeMinutes: number,
    readonly successful: boolean,
  ) {
    if (leadTimeMinutes < 0) {
      throw new IllegalArgumentError(
        'leadTimeMinutes must not be negative');
    }
  }
}
