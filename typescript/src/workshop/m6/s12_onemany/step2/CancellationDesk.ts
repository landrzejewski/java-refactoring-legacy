import type { Money } from '../../../shared/Money.js';
import type { LocalDateTime } from '../../../shared/time.js';
import type { TicketData } from '../TicketData.js';
import type { Refundable } from './Refundable.js';
import { RefundService } from './RefundService.js';
import { SingleTicket } from './SingleTicket.js';
import { TicketGroup } from './TicketGroup.js';

/** Krok 2: klient przeniesiony na nowy kontrakt; rozróżnienie jeden/wiele jeszcze widać. */
export class CancellationDesk {
  private readonly service = new RefundService();

  refund(tickets: readonly TicketData[], now: LocalDateTime): Money {
    const [first] = tickets;
    const refundable: Refundable = tickets.length === 1 && first !== undefined
      ? new SingleTicket(first)
      : TicketGroup.of(tickets);
    return this.service.refund(refundable, now);
  }
}
