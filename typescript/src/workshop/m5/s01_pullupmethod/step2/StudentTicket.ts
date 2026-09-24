import type { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Krok 2: price() nadpisuje teraz metodę abstrakcyjną z Ticket (override). */
export class StudentTicket extends Ticket {
  constructor(title: string, basePrice: Money) {
    super(title, basePrice);
  }

  override price(): Money {
    return this.basePrice().minus(this.basePrice().percent(25));
  }

  label(): string {
    return this.title() + ': ' + this.price();
  }
}
