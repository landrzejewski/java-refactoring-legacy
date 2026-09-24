import type { Money } from '../../shared/Money.js';
import type { Ticket } from './Ticket.js';

/** Stabilny kontrakt sceny: bilet normalny. */
export class StandardTicket implements Ticket {
  readonly #basePrice: Money;

  constructor(basePrice: Money) {
    this.#basePrice = basePrice;
  }

  basePrice(): Money {
    return this.#basePrice;
  }
}
