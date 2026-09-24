import { Money } from '../../../shared/Money.js';

/** Krok 2: liść jako niemutowalny obiekt wartości (odpowiednik rekordu). */
export class Product {
  readonly kind = 'product';
  readonly price: Money;

  constructor(readonly name: string, price: Money | string) {
    this.price = typeof price === 'string' ? Money.of(price) : price;
  }

  describe(): string {
    return `${this.name} ${this.price.toString()}`;
  }
}
