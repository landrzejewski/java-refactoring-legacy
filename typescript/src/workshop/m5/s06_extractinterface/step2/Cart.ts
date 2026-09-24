import { Decimal } from 'decimal.js';

import { Money } from '../../../shared/Money.js';
import type { Priceable } from './Priceable.js';

/**
 * Krok 2: klient przechodzi na rolę - jedna lista Priceable, jedno add(Priceable), jedna pętla.
 * Wywołujący musi zmienić addTicket/addSnack na add - w Javie zmiana była zgodna źródłowo,
 * ale NIE binarnie (zmienił się deskryptor add(...)).
 */
export class Cart {
  readonly #items: Priceable[] = [];

  add(item: Priceable): void {
    this.#items.push(item);
  }

  summary(): string {
    let total = Money.ZERO;
    let vat = Money.ZERO;
    for (const item of this.#items) {
      total = total.plus(item.price());
      const rate = new Decimal(item.vatPercent());
      vat = vat.plus(new Money(item.price().amount.times(rate)
        .dividedBy(rate.plus(100)).toDecimalPlaces(2, Decimal.ROUND_HALF_UP)));
    }
    return 'Razem: ' + total + ', VAT: ' + vat;
  }
}
