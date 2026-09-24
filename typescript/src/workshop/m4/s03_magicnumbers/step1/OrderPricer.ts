import { Decimal } from 'decimal.js';

import type { Order } from '../Order.js';

/*
 * Krok 1: Extract Constant dla kwot z cennika (ceny bazowe, poranek, VIP, opłata online).
 * Stałe modułu bez eksportu - jeden właściciel, niezmienny obiekt (Decimal),
 * a przy okazji koniec z tworzeniem Decimal w każdym obrocie pętli.
 */
const BASE_PRICE_2D = new Decimal('25.00');
const BASE_PRICE_3D = new Decimal('32.00');
const BASE_PRICE_IMAX = new Decimal('40.00');
const MORNING_REDUCTION = new Decimal('5.00');
const VIP_SURCHARGE = new Decimal('10.00');
const ONLINE_FEE_PER_TICKET = new Decimal('2.00');
const NO_FEE = new Decimal('0.00');

export class OrderPricer {
  summary(o: Order): string {
    let tickets = new Decimal(0);
    for (const t of o.tickets) {
      let p = o.format === 3 ? BASE_PRICE_IMAX
        : o.format === 2 ? BASE_PRICE_3D : BASE_PRICE_2D;
      if (t.type === 'S') {
        p = p.times(new Decimal(1).minus(new Decimal('0.25')));
      } else if (t.type === 'E') {
        p = p.times(new Decimal(1).minus(new Decimal('0.30')));
      } else if (t.type === 'C') {
        p = p.times(new Decimal(1).minus(new Decimal('0.40')));
      }
      if (o.start.hour < 12) {
        p = p.minus(MORNING_REDUCTION);
      }
      if (t.row >= 10) {
        p = p.plus(VIP_SURCHARGE);
      }
      tickets = tickets.plus(p);
    }
    if (o.tickets.length >= 10) {
      tickets = tickets.times(new Decimal('0.90'));
    }
    tickets = tickets.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    const fee = o.online
      ? ONLINE_FEE_PER_TICKET.times(o.tickets.length)
      : NO_FEE;
    const points = tickets.dividedBy(10).toDecimalPlaces(0, Decimal.ROUND_DOWN).toNumber();
    return 'Bilety: ' + tickets.toFixed(2) + ', oplata: ' + fee.toFixed(2) + ', razem: ' + tickets.plus(fee).toFixed(2)
      + ', punkty: ' + points;
  }
}
