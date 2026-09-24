import type { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Krok 2: price() odziedziczone z Ticket. */
export class StandardTicket extends Ticket {
  constructor(title: string, basePrice: Money) {
    super(title, basePrice);
  }

  protected override discountPercent(): number {
    return 0;
  }
}
