import type { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Krok 3: w podklasie zostaje tylko to, co naprawdę różne - cena. */
export class StandardTicket extends Ticket {
  constructor(title: string, basePrice: Money) {
    super(title, basePrice);
  }

  override price(): Money {
    return this.basePrice();
  }
}
