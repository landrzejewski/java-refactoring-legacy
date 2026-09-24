import type { Money } from '../../../shared/Money.js';
import type { LocalDateTime } from '../../../shared/time.js';
import type { TicketData } from '../TicketData.js';
import { RefundService } from './RefundService.js';
import { TicketGroup } from './TicketGroup.js';

/** Krok 3: rozróżnienie zniknęło - grupa jednego biletu zachowuje się jak bilet. */
export class CancellationDesk {
  private readonly service = new RefundService();

  refund(tickets: readonly TicketData[], now: LocalDateTime): Money {
    return this.service.refund(TicketGroup.of(tickets), now);
  }
}
