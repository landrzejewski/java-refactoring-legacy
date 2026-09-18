import { IllegalArgumentError } from '../../shared/errors.js';

export class DeploymentRiskInput {
  constructor(
    readonly changedFiles: number,
    readonly criticalServices: number,
    readonly failedChecks: number,
    readonly rollbackTested: boolean,
  ) {
    DeploymentRiskInput.requireNonNegative(changedFiles, 'changedFiles');
    DeploymentRiskInput.requireNonNegative(criticalServices, 'criticalServices');
    DeploymentRiskInput.requireNonNegative(failedChecks, 'failedChecks');
  }

  private static requireNonNegative(value: number, name: string): void {
    if (value < 0) {
      throw new IllegalArgumentError(name + ' must not be negative');
    }
  }
}
