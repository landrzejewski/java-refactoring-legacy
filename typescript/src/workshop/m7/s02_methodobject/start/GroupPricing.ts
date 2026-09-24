import { Money } from '../../../shared/Money.js';
import { LocalTime } from '../../../shared/time.js';
import type { GroupOrder } from '../GroupOrder.js';
import { Quote } from '../Quote.js';

/**
 * Start: długa metoda z ośmioma zmiennymi lokalnymi, które wzajemnie się karmią.
 * Extract Method na pętli nie przejdzie - pętla ma dwa wyjścia (tickets, count)
 * i trzy wejścia robocze (base, morning, glasses).
 */
export class GroupPricing {
  quote(order: GroupOrder): Quote {
    let base: Money;
    switch (order.format) {
      case 'IMAX': base = Money.of('40.00'); break;
      case '3D': base = Money.of('32.00'); break;
      default: base = Money.of('25.00');
    }
    const morning = order.start.isBefore(LocalTime.NOON);
    const glasses = order.format === '3D' && !order.ownGlasses;
    let tickets = Money.ZERO;
    let count = 0;
    for (const type of order.ticketTypes) {
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
    tickets = tickets.plus(Money.of('10.00').times(order.vipSeats));
    if (count >= 10) {
      tickets = tickets.minus(tickets.percent(10));
    }
    const fees = order.online ? Money.of('2.00').times(count) : Money.ZERO;
    const total = tickets.plus(fees);
    const points = Math.trunc(tickets.amount.trunc().toNumber() / 10);
    return new Quote(tickets, fees, total, points);
  }
}
