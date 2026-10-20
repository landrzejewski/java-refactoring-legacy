import type { Money } from '../../../shared/Money.js';

/** Krok 2: liść - produkt baru z ceną. (W Javie: rekord Product(name, price).) */
export class Product {
  constructor(
    readonly name: string,
    private readonly amount: Money,
  ) {}

  price(): Money {
    return this.amount;
  }

  render(depth: number, text: string[]): void {
    text.push(`${'  '.repeat(depth)}${this.name} ${this.amount.toString()}\n`);
  }
}
