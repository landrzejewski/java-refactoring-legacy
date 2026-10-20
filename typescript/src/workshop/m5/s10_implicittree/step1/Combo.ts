import { Money } from '../../../shared/Money.js';
import type { MenuItem } from './MenuItem.js';

/** Krok 1: węzeł - zestaw, którego cena to suma elementów. Niemutowalny. */
export class Combo {
  readonly items: readonly MenuItem[];

  constructor(readonly name: string, items: readonly MenuItem[]) {
    this.items = Object.freeze([...items]);
  }

  price(): Money {
    let total = Money.ZERO;
    for (const item of this.items) {
      total = total.plus(item.price());
    }
    return total;
  }

  render(depth: number, text: string[]): void {
    text.push(`${'  '.repeat(depth)}${this.name} ${this.price().toString()}\n`);
    this.items.forEach((item) => item.render(depth + 1, text));
  }
}
