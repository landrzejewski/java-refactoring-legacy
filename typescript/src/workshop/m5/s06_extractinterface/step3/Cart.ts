import { Money } from '../../../shared/Money.js';
import { type Priceable, vatAmount } from './Priceable.js';

/** Krok 3: koszyk pyta rolę o kwotę VAT pozycji zamiast liczyć ją sam. */
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
      vat = vat.plus(vatAmount(item));
    }
    return 'Razem: ' + total + ', VAT: ' + vat;
  }
}
