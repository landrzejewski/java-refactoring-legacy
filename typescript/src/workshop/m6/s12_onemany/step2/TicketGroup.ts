import { Money } from '../../../shared/Money.js';
import type { LocalDateTime } from '../../../shared/time.js';
import type { TicketData } from '../TicketData.js';
import type { Refundable } from './Refundable.js';
import { SingleTicket } from './SingleTicket.js';

/** Krok 2: węzeł - suma zwrotów elementów; pusta grupa daje 0.00. */
export class TicketGroup {
  readonly kind = 'group';
  readonly items: readonly Refundable[];

  constructor(items: readonly Refundable[]) {
    this.items = Object.freeze([...items]);
  }

  static of(tickets: readonly TicketData[]): TicketGroup {
    return new TicketGroup(tickets.map((ticket) => new SingleTicket(ticket)));
  }

  refundableAmount(now: LocalDateTime): Money {
    let total = Money.ZERO;
    for (const item of this.items) {
      total = total.plus(item.refundableAmount(now));
    }
    return total;
  }
}
