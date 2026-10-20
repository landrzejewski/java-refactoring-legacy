import { Decimal } from 'decimal.js';

import { Money } from '../../../shared/Money.js';
import type { TicketQuery } from '../TicketQuery.js';

/** Krok 1 (bez zmian): stary kalkulator - w trybie SHADOW jego wynik jest autorytatywny. */
export class LegacyPriceCalculator {
  price(q: TicketQuery): Money {
    let p = 0;
    if (q.format === '2D') {
      p = 25.00;
    } else if (q.format === '3D') {
      p = 32.00;
    } else if (q.format === 'IMAX') {
      p = 40.00;
    }
    if (q.type === 'STUDENT') {
      p = p - p * 0.25;
    } else if (q.type === 'SENIOR') {
      p = p - p * 0.30;
    } else if (q.type === 'CHILD') {
      p = p - p * 0.40;
    }
    if (q.start.hour < 12) {
      p = p - 5;
    }
    if (q.row >= 10) {
      p = p + 10;
    }
    if (q.format === '3D') {
      p = p + 3;
    }
    return new Money(new Decimal(p));
  }
}
