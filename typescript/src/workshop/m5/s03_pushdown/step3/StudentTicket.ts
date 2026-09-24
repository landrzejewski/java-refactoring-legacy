import type { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Krok 3: override rzucający UnsupportedOperationError zniknął - nie ma czego odmawiać. */
export class StudentTicket extends Ticket {
  constructor(basePrice: Money) {
    super(basePrice);
  }

  protected override discountPercent(): number {
    return 25;
  }
}
