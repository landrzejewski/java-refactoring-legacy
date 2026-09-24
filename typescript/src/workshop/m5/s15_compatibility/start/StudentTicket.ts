import type { Money } from '../../../shared/Money.js';
import { column } from '../Column.js';
import { Ticket } from './Ticket.js';

/** Start: kolumna "cena" zadeklarowana w podklasie (-25%). */
export class StudentTicket extends Ticket {
  constructor(title: string, basePrice: Money) {
    super(title, basePrice);
  }

  static {
    column(this.prototype.price, 'cena');
  }

  price(): Money {
    return this.basePrice().minus(this.basePrice().percent(25));
  }
}
