import { Money } from '../../../shared/Money.js';
import type { LocalDateTime } from '../../../shared/time.js';
import type { TicketData } from '../TicketData.js';
import type { Refundable } from './Refundable.js';
import { SingleTicket } from './SingleTicket.js';
import { TicketGroup } from './TicketGroup.js';

/**
 * Krok 2: Replace One/Many Distinctions with Composite - jedna metoda refund(Refundable).
 * Stare metody zostają jako cienkie delegacje, dopóki klienci nie przejdą na nowy kontrakt.
 */
export class RefundService {
  private static readonly FEE = Money.of('3.00');

  refund(refundable: Refundable, now: LocalDateTime): Money {
    return refundable.refundableAmount(now).minus(RefundService.FEE).max(Money.ZERO);
  }

  /**
   * Dawne refund(ticket, now) - TypeScript nie ma przeciążeń, więc stara metoda dostaje osobną nazwę.
   * @deprecated użyj {@link RefundService.refund} z {@link SingleTicket}.
   */
  refundTicket(ticket: TicketData, now: LocalDateTime): Money {
    return this.refund(new SingleTicket(ticket), now);
  }

  /** @deprecated użyj {@link RefundService.refund} z {@link TicketGroup}. */
  refundAll(tickets: readonly TicketData[], now: LocalDateTime): Money {
    return this.refund(TicketGroup.of(tickets), now);
  }
}
