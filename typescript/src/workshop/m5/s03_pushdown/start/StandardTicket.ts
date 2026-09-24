import type { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Start: jedyny bilet, który naprawdę korzysta z dopłaty VIP. */
export class StandardTicket extends Ticket {
  constructor(basePrice: Money) {
    super(basePrice);
  }

  protected override discountPercent(): number {
    return 0;
  }
}
