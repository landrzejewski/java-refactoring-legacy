import { Money } from '../../../shared/Money.js';
import type { MenuComponent } from './MenuComponent.js';

/** Krok 2: węzeł z dziećmi podanymi przy tworzeniu - add() nie istnieje nigdzie. */
export class Combo {
  readonly kind = 'combo';
  readonly children: readonly MenuComponent[];

  constructor(readonly name: string, children: readonly MenuComponent[]) {
    this.children = Object.freeze([...children]);
  }

  static of(name: string, ...children: MenuComponent[]): Combo {
    return new Combo(name, children);
  }

  get price(): Money {
    let total = Money.ZERO;
    for (const child of this.children) {
      total = total.plus(child.price);
    }
    return total;
  }

  describe(): string {
    const text = `${this.name} ${this.price.toString()}`;
    if (this.children.length === 0) {
      return text;
    }
    return `${text} [${this.children.map((child) => child.describe()).join(', ')}]`;
  }
}
