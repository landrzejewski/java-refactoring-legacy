import type { Money } from '../../../shared/Money.js';
import { column } from '../Column.js';
import { Ticket } from './Ticket.js';

/** Krok 1: bez zmian. */
export class StandardTicket extends Ticket {
  constructor(title: string, basePrice: Money) {
    super(title, basePrice);
  }

  static {
    column(this.prototype.price, 'cena');
  }

  price(): Money {
    return this.basePrice();
  }
}
