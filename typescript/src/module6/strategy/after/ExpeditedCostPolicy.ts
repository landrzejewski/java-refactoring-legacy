import { addExact } from '../../support.js';
import type { DeploymentCostPolicy } from './DeploymentCostPolicy.js';

export class ExpeditedCostPolicy implements DeploymentCostPolicy {
  calculate(baseCostInCents: number): number {
    return addExact(baseCostInCents, Math.trunc(baseCostInCents / 4));
  }
}
