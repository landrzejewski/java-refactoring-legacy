import type { Combo } from './Combo.js';
import type { Product } from './Product.js';

/**
 * Krok 2: jawny Composite - produkt (liść) albo zestaw (węzeł). Zamknięty zestaw typów
 * (w Javie: sealed interface) -> unia; obie klasy mają name, price() i render(depth, text).
 * render dopisuje linie do bufora text (w Javie: StringBuilder).
 */
export type MenuItem = Product | Combo;
