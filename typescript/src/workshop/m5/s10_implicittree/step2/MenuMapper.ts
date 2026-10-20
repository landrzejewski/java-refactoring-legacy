import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import { Combo } from './Combo.js';
import type { MenuItem } from './MenuItem.js';
import { Product } from './Product.js';

/**
 * Krok 2: mapper starego formatu (zagnieżdżone listy) na Composite. Zachowuje komunikaty
 * błędów starego kodu - to też jest obserwowalne zachowanie.
 */
export class MenuMapper {
  private constructor() {}

  static fromNested(combo: readonly unknown[]): Combo {
    const name = combo[0];
    if (combo.length === 0 || typeof name !== 'string') {
      throw new IllegalArgumentError('combo needs a name');
    }
    const items: MenuItem[] = [];
    for (const element of combo.slice(1)) {
      if (typeof element === 'string') {
        items.push(MenuMapper.product(element));
      } else if (Array.isArray(element)) {
        items.push(MenuMapper.fromNested(element));
      } else {
        throw new IllegalArgumentError(`unsupported element: ${String(element)}`);
      }
    }
    return new Combo(name, items);
  }

  private static product(text: string): Product {
    if (!text.includes('=')) {
      throw new IllegalArgumentError(`product needs a price: ${text}`);
    }
    const separator = text.indexOf('=');
    return new Product(text.substring(0, separator), Money.of(text.substring(separator + 1)));
  }
}
