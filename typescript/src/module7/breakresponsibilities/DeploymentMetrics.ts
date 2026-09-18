import { IllegalArgumentError } from '../../shared/errors.js';

export class DeploymentMetrics {
  constructor(
    readonly deployments: number,
    readonly failures: number,
    readonly averageLeadTimeMinutes: number,
  ) {
    if (deployments < 0) {
      throw new IllegalArgumentError(
        'deployments must not be negative');
    }
    if (failures < 0 || failures > deployments) {
      throw new IllegalArgumentError(
        'failures must be between 0 and deployments');
    }
    if (averageLeadTimeMinutes < 0) {
      throw new IllegalArgumentError(
        'averageLeadTimeMinutes must not be negative');
    }
    if (deployments === 0 && averageLeadTimeMinutes !== 0) {
      throw new IllegalArgumentError(
        'empty metrics must have zero average lead time');
    }
  }
}
