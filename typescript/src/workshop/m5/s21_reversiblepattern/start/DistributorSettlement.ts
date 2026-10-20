import { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Deal } from '../Deal.js';
import { FestivalTariffClient } from '../FestivalTariffClient.js';

/**
 * Start: dwa istniejące modele rozliczeń w jednym switchu. Model festiwalowy
 * dodatkowo tłumaczy obcy interfejs (grosze jako liczba całkowita) w środku logiki rozliczeń.
 * Oba warianty istnieją dziś i zmieniają się niezależnie - to uzasadnia wzorzec.
 */
export class DistributorSettlement {
  private readonly festival = new FestivalTariffClient();

  payout(deal: Deal, week: number, ticketRevenue: Decimal): Decimal {
    switch (deal.model) {
      case 'PERCENT': {
        const percent = week === 1 ? 50 : week === 2 ? 40 : 35;
        const share = ticketRevenue.times(percent)
          .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
        return Decimal.max(share, new Decimal('500.00'));
      }
      case 'FESTIVAL': {
        const cents = this.festival.weeklyFeeInCents(deal.title, week);
        return new Decimal(cents).dividedBy(100);
      }
      default:
        throw new IllegalArgumentError(`nieznany model: ${deal.model}`);
    }
  }
}
