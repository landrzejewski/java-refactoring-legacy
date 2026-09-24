import type { Money } from '../../../shared/Money.js';
import { StandardTicket } from '../StandardTicket.js';
import type { PriceRule, TicketType } from './PriceRule.js';

/** Krok 2: reguła mówi wprost, jaki bilet obsługuje. */
export class StandardRule implements PriceRule<StandardTicket> {
  ticketType(): TicketType<StandardTicket> {
    return StandardTicket;
  }

  apply(ticket: StandardTicket): Money {
    return ticket.basePrice();
  }
}
