import { Money } from '../../../shared/Money.js';
import { Duration, type LocalDateTime } from '../../../shared/time.js';
import type { TicketData } from '../TicketData.js';

/** Krok 3: liść - reguła zwrotu jednego biletu (przeniesiona z ticketShare). */
export class SingleTicket {
  readonly kind = 'single';

  constructor(readonly ticket: TicketData) {}

  refundableAmount(now: LocalDateTime): Money {
    if (!now.isBefore(this.ticket.showStart)) {
      return Money.ZERO;
    }
    if (Duration.between(now, this.ticket.showStart).toHours() >= 24) {
      return this.ticket.price;
    }
    return this.ticket.price.percent(50);
  }
}
