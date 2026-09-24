import { Decimal } from 'decimal.js';

import type { Deal } from '../Deal.js';
import type { SettlementModel } from './SettlementModel.js';

/** Krok 1: procent od przychodu - tydzień 1: 50%, 2: 40%, dalej 35%; minimalna gwarancja 500.00. */
export class PercentageModel implements SettlementModel {
  private static readonly MINIMUM_GUARANTEE = new Decimal('500.00');

  payout(_deal: Deal, week: number, ticketRevenue: Decimal): Decimal {
    const percent = week === 1 ? 50 : week === 2 ? 40 : 35;
    const share = ticketRevenue.times(percent)
      .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    return Decimal.max(share, PercentageModel.MINIMUM_GUARANTEE);
  }
}
