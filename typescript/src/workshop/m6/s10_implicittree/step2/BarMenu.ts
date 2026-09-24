import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import { MenuMapper } from './MenuMapper.js';

/** Krok 2: price przeniesione na Composite - jedna operacja naraz; render jeszcze stary. */
export class BarMenu {
  price(combo: readonly unknown[]): Money {
    return MenuMapper.fromNested(combo).price();
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

  private static productPrice(product: string): Money {
    if (!product.includes('=')) {
      throw new IllegalArgumentError(`product needs a price: ${product}`);
    }
    return Money.of(product.substring(product.indexOf('=') + 1));
  }
}
