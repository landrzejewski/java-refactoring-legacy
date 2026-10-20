import type { Money } from '../../shared/Money.js';
import type { LocalDate } from '../../shared/time.js';

/** Stabilny kontrakt sceny: sprzedaż biletów na jeden seans danego dnia. */
export class ScreeningSale {
  constructor(
    readonly day: LocalDate,
    readonly title: string,
    readonly tickets: number,
    readonly revenue: Money,
  ) {}
}
