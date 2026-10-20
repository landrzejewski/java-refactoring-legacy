import type { Money } from '../../../../shared/Money.js';
import type { Ticket } from './Ticket.js';

/** Bilet na zwykłe miejsce. Publiczny konstruktor - klienci robią new. */
export class StandardTicket implements Ticket {
  constructor(
    private readonly title: string,
    private readonly base: Money,
    private readonly row: number,
  ) {}

  price(): Money {
    return this.base;
  }

  describe(): string {
    return `${this.title} r${this.row} ${this.price().toString()}`;
  }
}
