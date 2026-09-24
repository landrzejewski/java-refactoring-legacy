import { Decimal } from 'decimal.js';

import { Money } from '../../../shared/Money.js';
import type { Snack } from './Snack.js';
import type { Ticket } from './Ticket.js';

/**
 * Start: koszyk ma dwie listy, dwie metody addTicket/addSnack (w Javie dwa przeciążenia add(...))
 * i dwie kopie liczenia VAT. Każdy nowy rodzaj pozycji (np. okulary 3D) to kolejna lista i kolejna pętla.
 */
export class Cart {
  readonly #tickets: Ticket[] = [];
  readonly #snacks: Snack[] = [];

  addTicket(ticket: Ticket): void {
    this.#tickets.push(ticket);
  }

  addSnack(snack: Snack): void {
    this.#snacks.push(snack);
  }

  summary(): string {
    let total = Money.ZERO;
    let vat = Money.ZERO;
    for (const ticket of this.#tickets) {
      total = total.plus(ticket.price());
      const rate = new Decimal(ticket.vatPercent());
      vat = vat.plus(new Money(ticket.price().amount.times(rate)
        .dividedBy(rate.plus(100)).toDecimalPlaces(2, Decimal.ROUND_HALF_UP)));
    }
    for (const snack of this.#snacks) {
      total = total.plus(snack.price());
      const rate = new Decimal(snack.vatPercent());
      vat = vat.plus(new Money(snack.price().amount.times(rate)
        .dividedBy(rate.plus(100)).toDecimalPlaces(2, Decimal.ROUND_HALF_UP)));
    }
    return 'Razem: ' + total + ', VAT: ' + vat;
  }
}
