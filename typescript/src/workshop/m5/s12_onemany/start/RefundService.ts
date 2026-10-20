import { Money } from '../../../shared/Money.js';
import { Duration, type LocalDateTime } from '../../../shared/time.js';
import type { TicketData } from '../TicketData.js';

/**
 * Start: osobna obsługa jednego biletu i listy biletów. Reguła zwrotu (100% / 50% / 0)
 * jest napisana dwa razy - trochę inaczej, więc łatwo o rozjazd przy następnej zmianie.
 */
export class RefundService {
  private static readonly FEE = Money.of('3.00');

  refund(ticket: TicketData, now: LocalDateTime): Money {
    let amount: Money;
    if (!now.isBefore(ticket.showStart)) {
      amount = Money.ZERO;
    } else if (Duration.between(now, ticket.showStart).toHours() >= 24) {
      amount = ticket.price;
    } else {
      amount = ticket.price.percent(50);
    }
    return amount.minus(RefundService.FEE).max(Money.ZERO);
  }

  refundAll(tickets: readonly TicketData[], now: LocalDateTime): Money {
    let total = Money.ZERO;
    for (const ticket of tickets) {
      if (now.isBefore(ticket.showStart)) {
        const hours = Duration.between(now, ticket.showStart).toHours();
        total = total.plus(hours >= 24 ? ticket.price : ticket.price.percent(50));
      }
    }
    return total.minus(RefundService.FEE).max(Money.ZERO);
  }
}
