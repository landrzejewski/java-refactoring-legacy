import { format } from 'node:util';

import type { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Start: bilet studencki (-25%) - ta sama etykieta, ale przez util.format (odpowiednik String.format). */
export class StudentTicket extends Ticket {
  constructor(title: string, basePrice: Money) {
    super(title, basePrice);
  }

  price(): Money {
    return this.basePrice().minus(this.basePrice().percent(25));
  }

  label(): string {
    return format('%s: %s', this.title(), this.price());
  }
}
