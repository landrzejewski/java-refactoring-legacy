import type { Money } from '../../shared/Money.js';

/** Stabilny kontrakt sceny: sprzedaż miejsc na jeden seans (rząd 10+ to VIP). */
export class SeatSale {
  constructor(
    readonly title: string,
    readonly base: Money,
    readonly rows: readonly number[],
  ) {}
}
