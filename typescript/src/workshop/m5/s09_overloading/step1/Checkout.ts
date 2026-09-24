import { Money } from '../../../shared/Money.js';
import { PriceList } from './PriceList.js';
import type { Ticket } from './Ticket.js';

/** Krok 1: bez zmian - ten sam kod klienta liczy teraz poprawnie. */
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
    return cart.includes(ticket);
  }
}
