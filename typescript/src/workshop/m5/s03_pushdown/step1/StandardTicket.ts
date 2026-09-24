import type { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Krok 1: bez zmian. */
export class StandardTicket extends Ticket {
  constructor(basePrice: Money) {
    super(basePrice);
  }

  protected override discountPercent(): number {
    return 0;
  }
}
