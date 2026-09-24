import type { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Start: bilet normalny - etykieta sklejana operatorem +. */
export class StandardTicket extends Ticket {
  constructor(title: string, basePrice: Money) {
    super(title, basePrice);
  }

  price(): Money {
    return this.basePrice();
  }

  label(): string {
    return this.title() + ': ' + this.price();
  }
}
