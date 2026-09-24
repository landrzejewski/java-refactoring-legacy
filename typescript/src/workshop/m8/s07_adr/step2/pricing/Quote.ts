import type { Money } from '../../../../shared/Money.js';

/** Krok 2: kwota w Money (R2). */
export class Quote {
  constructor(readonly total: Money, readonly groupDiscount: boolean) {}
}
