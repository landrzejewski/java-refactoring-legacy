import type { DeploymentCostPolicy } from './DeploymentCostPolicy.js';

export class StandardCostPolicy implements DeploymentCostPolicy {
  calculate(baseCostInCents: number): number {
    return baseCostInCents;
  }
}
