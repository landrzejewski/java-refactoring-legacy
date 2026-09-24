import type { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Krok 3: bez zmian. */
export class StudentTicket extends Ticket {
  constructor(title: string, basePrice: Money) {
    super(title, basePrice);
  }

  protected override discountPercent(): number {
    return 25;
  }
}
