import type { Money } from '../../../../shared/Money.js';

/** Krok 1: wynik przypadku użycia. */
export class Booking {
  constructor(readonly id: string, readonly total: Money) {}
}
