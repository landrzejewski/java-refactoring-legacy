import { assertNever } from '../../../shared/assertNever.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { CustomerTier } from '../CustomerTier.js';

/**
 * Strategy: algorytm kosztu wsparcia. Wybierany raz przez klienta, potem tylko wywoływany.
 */
export interface SupportCostPolicy {
  cost(hours: number): number;
}

const STANDARD: SupportCostPolicy = { cost: hours => hours * 100 };
const PREMIUM: SupportCostPolicy = { cost: hours => 500 + hours * 80 };
const ENTERPRISE: SupportCostPolicy = { cost: hours => Math.max(2_000, hours * 60) };

// Odpowiednik stałych i statycznej metody interfejsu SupportCostPolicy.
export const SupportCostPolicy = Object.freeze({
  STANDARD,
  PREMIUM,
  ENTERPRISE,

  forTier(tier: CustomerTier): SupportCostPolicy {
    const checkedTier = requireNonNull(tier, 'tier');
    switch (checkedTier) {
      case CustomerTier.STANDARD:
        return STANDARD;
      case CustomerTier.PREMIUM:
        return PREMIUM;
      case CustomerTier.ENTERPRISE:
        return ENTERPRISE;
      default:
        return assertNever(checkedTier);
    }
  },
});
