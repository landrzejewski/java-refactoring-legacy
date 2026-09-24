import { Money } from '../../../../shared/Money.js';
import type { Ticket } from './Ticket.js';

/** Krok 2: bez zmian. Bilet na miejsce VIP: +10.00. Publiczna klasa z publicznym konstruktorem. */
export class VipTicket implements Ticket {
  constructor(
    private readonly title: string,
    private readonly base: Money,
    private readonly row: number,
  ) {}

  price(): Money {
    return this.base.plus(Money.of('10.00'));
  }

  describe(): string {
    return `${this.title} r${this.row} VIP ${this.price().toString()}`;
  }
}
