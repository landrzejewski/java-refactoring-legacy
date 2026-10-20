import type { Money } from '../../../shared/Money.js';

/** Krok 3: produkt baru (VAT 23%) - czyste dane, bez accept. */
export class SnackItem {
  readonly kind = 'snack';

  constructor(readonly name: string, readonly price: Money) {}
}
