import type { Money } from '../../../shared/Money.js';

/**
 * Krok 1: Extract Delegate - współdzielona IMPLEMENTACJA (naliczanie i wydawanie punktów)
 * dostaje własną klasę. Reużycie kodu nie wymaga już dziedziczenia.
 */
export class PointsLedger {
  #points = 0;

  earn(paidForTickets: Money): void {
    this.#points += paidForTickets.amount.dividedToIntegerBy(10).toNumber();
  }

  spend(amount: number): boolean {
    if (this.#points < amount) {
      return false;
    }
    this.#points -= amount;
    return true;
  }

  points(): number {
    return this.#points;
  }
}
