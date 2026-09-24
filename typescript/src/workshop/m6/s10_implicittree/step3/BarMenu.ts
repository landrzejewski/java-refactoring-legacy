import type { Money } from '../../../shared/Money.js';
import { MenuMapper } from './MenuMapper.js';

/**
 * Krok 3: render też na Composite. BarMenu tylko mapuje stary format na drzewo - format
 * trwały (zagnieżdżone listy) zmienimy osobnym krokiem, jeśli w ogóle.
 */
export class BarMenu {
  price(combo: readonly unknown[]): Money {
    return MenuMapper.fromNested(combo).price();
  }

  render(combo: readonly unknown[]): string {
    const text: string[] = [];
    MenuMapper.fromNested(combo).render(0, text);
    return text.join('');
  }
}
