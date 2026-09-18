import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { DeploymentCostPolicy } from './DeploymentCostPolicy.js';

export class DeploymentCostCalculator {
  private readonly policy: DeploymentCostPolicy;

  constructor(policy: DeploymentCostPolicy) {
    this.policy = requireNonNull(policy, 'policy');
  }

  calculate(baseCostInCents: number): number {
    if (baseCostInCents < 0) {
      throw new IllegalArgumentError('base cost must not be negative');
    }
    return this.policy.calculate(baseCostInCents);
  }
}
