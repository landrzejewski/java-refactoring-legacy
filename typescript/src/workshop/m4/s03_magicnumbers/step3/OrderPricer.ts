import { Decimal } from 'decimal.js';

import type { Order } from '../Order.js';

/*
 * Krok 3 (rozwiązanie): Extract Constant dla stawek zniżek wg typu biletu.
 * Wszystkie liczby z cennika mają teraz nazwę i jednego właściciela;
 * pętla czyta się jak regulamin.
 * Stałe nie są eksportowane - to szczegół tego modułu, nie API dla klientów.
 */
const BASE_PRICE_2D = new Decimal('25.00');
const BASE_PRICE_3D = new Decimal('32.00');
const BASE_PRICE_IMAX = new Decimal('40.00');
const MORNING_REDUCTION = new Decimal('5.00');
const VIP_SURCHARGE = new Decimal('10.00');
const ONLINE_FEE_PER_TICKET = new Decimal('2.00');
const STUDENT_DISCOUNT = new Decimal('0.25');
const SENIOR_DISCOUNT = new Decimal('0.30');
const CHILD_DISCOUNT = new Decimal('0.40');
const NO_FEE = new Decimal('0.00');
const MORNING_ENDS_AT_HOUR = 12;
const VIP_FROM_ROW = 10;
const GROUP_MIN_TICKETS = 10;
const GROUP_PRICE_FACTOR = new Decimal('0.90');
const AMOUNT_PER_LOYALTY_POINT = new Decimal(10);

export class OrderPricer {
  summary(o: Order): string {
    let tickets = new Decimal(0);
    for (const t of o.tickets) {
      let p = o.format === 3 ? BASE_PRICE_IMAX
        : o.format === 2 ? BASE_PRICE_3D : BASE_PRICE_2D;
      if (t.type === 'S') {
        p = p.times(new Decimal(1).minus(STUDENT_DISCOUNT));
      } else if (t.type === 'E') {
        p = p.times(new Decimal(1).minus(SENIOR_DISCOUNT));
      } else if (t.type === 'C') {
        p = p.times(new Decimal(1).minus(CHILD_DISCOUNT));
      }
      if (o.start.hour < MORNING_ENDS_AT_HOUR) {
        p = p.minus(MORNING_REDUCTION);
      }
      if (t.row >= VIP_FROM_ROW) {
        p = p.plus(VIP_SURCHARGE);
      }
      tickets = tickets.plus(p);
    }
    if (o.tickets.length >= GROUP_MIN_TICKETS) {
      tickets = tickets.times(GROUP_PRICE_FACTOR);
    }
    tickets = tickets.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    const fee = o.online
      ? ONLINE_FEE_PER_TICKET.times(o.tickets.length)
      : NO_FEE;
    const points = tickets.dividedBy(AMOUNT_PER_LOYALTY_POINT).toDecimalPlaces(0, Decimal.ROUND_DOWN).toNumber();
    return 'Bilety: ' + tickets.toFixed(2) + ', oplata: ' + fee.toFixed(2) + ', razem: ' + tickets.plus(fee).toFixed(2)
      + ', punkty: ' + points;
  }
}
