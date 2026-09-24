import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';

/**
 * Start: ukryte drzewo - zestaw combo to lista, której pierwszy element jest nazwą, a kolejne
 * to "nazwa=cena" albo zagnieżdżone listy. Każda operacja powtarza sprawdzanie typu elementu (typeof, Array.isArray),
 * a render dla każdego węzła liczy cenę od nowa (koszt kwadratowy).
 */
export class BarMenu {
  price(combo: readonly unknown[]): Money {
    BarMenu.requireName(combo);
    let total = Money.ZERO;
    for (const element of combo.slice(1)) {
      if (typeof element === 'string') {
        total = total.plus(BarMenu.productPrice(element));
      } else if (Array.isArray(element)) {
        total = total.plus(this.price(element));
      } else {
        throw new IllegalArgumentError(`unsupported element: ${String(element)}`);
      }
    }
    return total;
  }

  render(combo: readonly unknown[]): string {
    const text: string[] = [];
    this.renderTo(combo, 0, text);
    return text.join('');
  }

  private renderTo(combo: readonly unknown[], depth: number, text: string[]): void {
    text.push(`${'  '.repeat(depth)}${String(combo[0])} ${this.price(combo).toString()}\n`);
    for (const element of combo.slice(1)) {
      if (typeof element === 'string') {
        text.push(`${'  '.repeat(depth + 1)}${element.substring(0, element.indexOf('='))}`
          + ` ${BarMenu.productPrice(element).toString()}\n`);
      } else if (Array.isArray(element)) {
        this.renderTo(element, depth + 1, text);
      } else {
        throw new IllegalArgumentError(`unsupported element: ${String(element)}`);
      }
    }
  }

  private static requireName(combo: readonly unknown[]): void {
    if (combo.length === 0 || typeof combo[0] !== 'string') {
      throw new IllegalArgumentError('combo needs a name');
    }
  }

  private static productPrice(product: string): Money {
    if (!product.includes('=')) {
      throw new IllegalArgumentError(`product needs a price: ${product}`);
    }
    return Money.of(product.substring(product.indexOf('=') + 1));
  }
}
