import { Decimal } from 'decimal.js';

import type { Order } from '../Order.js';

/**
 * Start: podsumowanie zamówienia pełne magicznych liczb. Cztery różne "dziesiątki":
 * rząd VIP, dopłata VIP, próg grupy i kwota za punkt lojalnościowy. To cztery różne decyzje.
 */
export class OrderPricer {
  summary(o: Order): string {
    let tickets = new Decimal(0);
    for (const t of o.tickets) {
      let p = o.format === 3 ? new Decimal('40.00')
        : o.format === 2 ? new Decimal('32.00') : new Decimal('25.00');
      if (t.type === 'S') {
        p = p.times(new Decimal(1).minus(new Decimal('0.25')));
      } else if (t.type === 'E') {
        p = p.times(new Decimal(1).minus(new Decimal('0.30')));
      } else if (t.type === 'C') {
        p = p.times(new Decimal(1).minus(new Decimal('0.40')));
      }
      if (o.start.hour < 12) {
        p = p.minus(new Decimal('5.00'));
      }
      if (t.row >= 10) {
        p = p.plus(new Decimal('10.00'));
      }
      tickets = tickets.plus(p);
    }
    if (o.tickets.length >= 10) {
      tickets = tickets.times(new Decimal('0.90'));
    }
    tickets = tickets.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    const fee = o.online
      ? new Decimal('2.00').times(o.tickets.length)
      : new Decimal('0.00');
    const points = tickets.dividedBy(10).toDecimalPlaces(0, Decimal.ROUND_DOWN).toNumber();
    return 'Bilety: ' + tickets.toFixed(2) + ', oplata: ' + fee.toFixed(2) + ', razem: ' + tickets.plus(fee).toFixed(2)
      + ', punkty: ' + points;
  }
}
