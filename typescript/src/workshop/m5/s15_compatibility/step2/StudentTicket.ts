import type { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Krok 2: price() odziedziczone z Ticket, tu tylko zniżka. */
export class StudentTicket extends Ticket {
  constructor(title: string, basePrice: Money) {
    super(title, basePrice);
  }

  protected override discountPercent(): number {
    return 25;
  }
}
