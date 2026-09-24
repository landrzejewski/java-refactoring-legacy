import type { Money } from '../../../shared/Money.js';
import type { PricedTicket } from './PricedTicket.js';

/** Krok 3: czysty rdzeń - tylko to, co ma każdy bilet. Bez flag. (W Javie: rekord.) */
export class Ticket implements PricedTicket {
  constructor(
    readonly title: string,
    readonly format: string,
    readonly base: Money,
  ) {}

  price(): Money {
    return this.base;
  }

  description(): string {
    return `${this.title} ${this.format}`;
  }
}
