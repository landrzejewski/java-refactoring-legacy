import { assertNever } from '../../../shared/assertNever.js';
import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { addExact } from '../../support.js';

// Odpowiednik zagnieżdżonego enuma LegacyDeploymentCostCalculator.DeploymentMode.
export enum DeploymentMode {
  STANDARD = 'STANDARD',
  EXPEDITED = 'EXPEDITED',
}

export class LegacyDeploymentCostCalculator {
  calculate(baseCostInCents: number, mode: DeploymentMode): number {
    if (baseCostInCents < 0) {
      throw new IllegalArgumentError('base cost must not be negative');
    }

    const checkedMode = requireNonNull(mode, 'mode');
    switch (checkedMode) {
      case DeploymentMode.STANDARD:
        return baseCostInCents;
      case DeploymentMode.EXPEDITED:
        return addExact(baseCostInCents, Math.trunc(baseCostInCents / 4));
      default:
        return assertNever(checkedMode);
    }
  }
}
