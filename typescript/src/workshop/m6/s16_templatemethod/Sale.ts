import type { Money } from '../../shared/Money.js';
import type { LocalTime } from '../../shared/time.js';

/** Stabilny kontrakt sceny: sprzedaż na jeden seans dnia. */
export class Sale {
  constructor(
    readonly time: LocalTime,
    readonly title: string,
    readonly tickets: number,
    readonly amount: Money,
  ) {}
}
