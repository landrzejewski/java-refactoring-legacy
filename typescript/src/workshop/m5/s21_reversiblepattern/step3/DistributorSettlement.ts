import { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Deal } from '../Deal.js';

/**
 * Krok 3 (rozwiązanie): refaktoryzacja OD wzorca. Inline Class PercentageModel,
 * Safe Delete interfejsu SettlementModel i mapy. Jeden algorytm = jedna metoda.
 * Gdy wróci drugi model, Strategy da się przywrócić tymi samymi krokami w przód.
 */
export class DistributorSettlement {
  private static readonly MINIMUM_GUARANTEE = new Decimal('500.00');

  payout(deal: Deal, week: number, ticketRevenue: Decimal): Decimal {
    if (deal.model !== 'PERCENT') {
      throw new IllegalArgumentError(`nieznany model: ${deal.model}`);
    }
    const percent = week === 1 ? 50 : week === 2 ? 40 : 35;
    const share = ticketRevenue.times(percent)
      .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    return Decimal.max(share, DistributorSettlement.MINIMUM_GUARANTEE);
  }
}
