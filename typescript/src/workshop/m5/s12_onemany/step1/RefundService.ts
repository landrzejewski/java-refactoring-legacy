import { Money } from '../../../shared/Money.js';
import { Duration, type LocalDateTime } from '../../../shared/time.js';
import type { TicketData } from '../TicketData.js';

/** Krok 1: Extract Method ticketShare - reguła zwrotu jednego biletu w jednym miejscu. */
export class RefundService {
  private static readonly FEE = Money.of('3.00');

  refund(ticket: TicketData, now: LocalDateTime): Money {
    return RefundService.ticketShare(ticket, now).minus(RefundService.FEE).max(Money.ZERO);
  }

  refundAll(tickets: readonly TicketData[], now: LocalDateTime): Money {
    let total = Money.ZERO;
    for (const ticket of tickets) {
      total = total.plus(RefundService.ticketShare(ticket, now));
    }
    return total.minus(RefundService.FEE).max(Money.ZERO);
  }

  private static ticketShare(ticket: TicketData, now: LocalDateTime): Money {
    if (!now.isBefore(ticket.showStart)) {
      return Money.ZERO;
    }
    if (Duration.between(now, ticket.showStart).toHours() >= 24) {
      return ticket.price;
    }
    return ticket.price.percent(50);
  }
}
