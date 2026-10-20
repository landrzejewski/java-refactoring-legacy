import { Decimal } from 'decimal.js';

import { Money } from '../../../shared/Money.js';
import type { TicketQuery } from '../TicketQuery.js';

/**
 * Start: cennik, do którego trzeba dodać promocję "Tani wtorek" (NORMAL -20% we wtorek).
 * Zniżki są wplecione w jedną metodę i zależą tylko od typu biletu - nowa reguła
 * potrzebuje daty. Pokusa: jeden duży commit "porządki + tani wtorek".
 */
export class PriceList {
  price(q: TicketQuery): Money {
    let p: Decimal;
    if (q.format === 'IMAX') {
      p = new Decimal('40.00');
    } else if (q.format === '3D') {
      p = new Decimal('32.00');
    } else {
      p = new Decimal('25.00');
    }
    let d: Decimal;
    if (q.type === 'STUDENT') {
      d = p.times(new Decimal('0.25'));
    } else if (q.type === 'SENIOR') {
      d = p.times(new Decimal('0.30'));
    } else if (q.type === 'CHILD') {
      d = p.times(new Decimal('0.40'));
    } else {
      d = new Decimal(0);
    }
    p = p.minus(d);
    if (q.start.hour < 12) {
      p = p.minus(new Decimal('5.00'));
    }
    if (q.row >= 10) {
      p = p.plus(new Decimal('10.00'));
    }
    return new Money(p);
  }
}
