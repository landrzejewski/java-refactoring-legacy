import type { Decimal } from 'decimal.js';

/** Krok 2: wynik wyceny - suma i liczba miejsc VIP (potrzebna w powiadomieniu). */
export class Pricing {
  constructor(readonly total: Decimal, readonly vipSeats: number) {}
}
