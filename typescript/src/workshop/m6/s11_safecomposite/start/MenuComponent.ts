import { UnsupportedOperationError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';

/**
 * Start: Transparent Composite - add() i children() we wspólnym typie. Klient może wywołać
 * add() na liściu; kompilator milczy, błąd wychodzi dopiero w runtime.
 */
export abstract class MenuComponent {
  abstract name(): string;

  abstract price(): Money;

  add(_child: MenuComponent): void {
    throw new UnsupportedOperationError(`cannot add to ${this.name()}`);
  }

  children(): readonly MenuComponent[] {
    return Object.freeze([]);
  }

  describe(): string {
    const text = `${this.name()} ${this.price().toString()}`;
    if (this.children().length === 0) {
      return text;
    }
    return `${text} [${this.children().map((child) => child.describe()).join(', ')}]`;
  }
}
