import { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Krok 3: w podklasie zostaje tylko to, co naprawdę różne - cena (+10.00 za VIP). */
export class VipTicket extends Ticket {
  constructor(title: string, basePrice: Money) {
    super(title, basePrice);
  }

  override price(): Money {
    return this.basePrice().plus(Money.of('10.00'));
  }
}
