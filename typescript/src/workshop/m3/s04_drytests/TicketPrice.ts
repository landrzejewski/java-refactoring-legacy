import { Decimal } from 'decimal.js';

import type { LocalTime } from '../../shared/time.js';
import type { Tariff } from './Tariff.js';

/** Kod produkcyjny sceny (stabilny): cena biletu według taryfy, minus 5.00 za seans poranny. */
export class TicketPrice {
  constructor(private readonly tariffValue: Tariff) {}

  tariff(): Tariff {
    return this.tariffValue;
  }

  of(format: string, type: string, start: LocalTime): Decimal {
    const base = this.tariffValue.basePrices.get(format)!;
    const discount = base.times(this.tariffValue.discountPercents.get(type)!)
      .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    let price = base.minus(discount);
    if (start.hour < 12) {
      price = price.minus(new Decimal('5.00'));
    }
    return price.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
