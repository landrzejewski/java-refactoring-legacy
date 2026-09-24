import { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Start: bilet na miejsce VIP (+10.00) - ta sama etykieta, ale przez tablicę i join (odpowiednik StringBuilder). */
export class VipTicket extends Ticket {
  constructor(title: string, basePrice: Money) {
    super(title, basePrice);
  }

  price(): Money {
    return this.basePrice().plus(Money.of('10.00'));
  }

  label(): string {
    return [this.title(), ': ', this.price()].join('');
  }
}
