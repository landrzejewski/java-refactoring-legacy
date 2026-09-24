import { Money } from '../../../shared/Money.js';
import { containsEqual } from '../Equatable.js';
import { PriceList } from './PriceList.js';
import type { Ticket } from './Ticket.js';

/** Krok 2: alreadyInCart pyta o równość przez kontrakt Equatable (w Javie contains() samo woła equals(Object)). */
export class Checkout {
  readonly #priceList = new PriceList();

  total(tickets: readonly Ticket[]): Money {
    let total = Money.ZERO;
    for (const ticket of tickets) {
      total = total.plus(this.#priceList.price(ticket));
    }
    return total;
  }

  alreadyInCart(cart: readonly Ticket[], ticket: Ticket): boolean {
    return containsEqual(cart, ticket);
  }
}
