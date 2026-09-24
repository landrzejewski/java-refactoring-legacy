import type { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Krok 1: ujednolicenie ciała `label()` - teraz tekstowo identyczne jak w StandardTicket. */
export class StudentTicket extends Ticket {
  constructor(title: string, basePrice: Money) {
    super(title, basePrice);
  }

  price(): Money {
    return this.basePrice().minus(this.basePrice().percent(25));
  }

  label(): string {
    return this.title() + ': ' + this.price();
  }
}
