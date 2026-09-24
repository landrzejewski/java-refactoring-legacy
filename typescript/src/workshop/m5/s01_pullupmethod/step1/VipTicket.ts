import { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Krok 1: ujednolicenie ciała `label()` - teraz tekstowo identyczne jak w StandardTicket. */
export class VipTicket extends Ticket {
  constructor(title: string, basePrice: Money) {
    super(title, basePrice);
  }

  price(): Money {
    return this.basePrice().plus(Money.of('10.00'));
  }

  label(): string {
    return this.title() + ': ' + this.price();
  }
}
