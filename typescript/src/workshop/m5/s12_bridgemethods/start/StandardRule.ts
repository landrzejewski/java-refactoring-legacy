import type { Money } from '../../../shared/Money.js';
import type { StandardTicket } from '../StandardTicket.js';

/** Start: reguła cenowa bez wspólnego typu - rejestr znajduje jej typ biletu po nazwie klasy. */
export class StandardRule {
  apply(ticket: StandardTicket): Money {
    return ticket.basePrice();
  }
}
