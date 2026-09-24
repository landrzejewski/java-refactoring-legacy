import { Money } from '../../../shared/Money.js';
import { LocalTime } from '../../../shared/time.js';
import type { GroupOrder } from '../GroupOrder.js';
import { Quote } from '../Quote.js';

/**
 * Krok 1: obiekt metody - jedno wykonanie wyceny. Nie jest usługą współdzieloną:
 * powstaje dla każdego wywołania. Ciało skopiowane 1:1 z GroupPricing.quote().
 * Eksportowany tylko na potrzeby GroupPricing (w Javie klasa pakietowa).
 */
export class GroupQuoteCalculation {
  constructor(private readonly order: GroupOrder) {}

  calculate(): Quote {
    let base: Money;
    switch (this.order.format) {
      case 'IMAX': base = Money.of('40.00'); break;
      case '3D': base = Money.of('32.00'); break;
      default: base = Money.of('25.00');
    }
    const morning = this.order.start.isBefore(LocalTime.NOON);
    const glasses = this.order.format === '3D' && !this.order.ownGlasses;
    let tickets = Money.ZERO;
    let count = 0;
    for (const type of this.order.ticketTypes) {
      let discount: number;
      switch (type) {
        case 'STUDENT': discount = 25; break;
        case 'SENIOR': discount = 30; break;
        case 'CHILD': discount = 40; break;
        default: discount = 0;
      }
      let price = base.minus(base.percent(discount));
      if (morning) {
        price = price.minus(Money.of('5.00'));
      }
      if (glasses) {
        price = price.plus(Money.of('3.00'));
      }
      tickets = tickets.plus(price);
      count++;
    }
    tickets = tickets.plus(Money.of('10.00').times(this.order.vipSeats));
    if (count >= 10) {
      tickets = tickets.minus(tickets.percent(10));
    }
    const fees = this.order.online ? Money.of('2.00').times(count) : Money.ZERO;
    const total = tickets.plus(fees);
    const points = Math.trunc(tickets.amount.trunc().toNumber() / 10);
    return new Quote(tickets, fees, total, points);
  }
}
