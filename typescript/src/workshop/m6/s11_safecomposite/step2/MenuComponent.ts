import type { Combo } from './Combo.js';
import type { Product } from './Product.js';

/**
 * Krok 2: forma zamknięta (odpowiednik sealed interface z Javy 25) - unia znanych węzłów,
 * niemutowalne obiekty, brak mutacji po zbudowaniu.
 */
export type MenuComponent = Product | Combo;
