import type { Money } from '../../shared/Money.js';

/** Stabilny kontrakt sceny: jedna sprzedaż biletów na film. */
export class Sale {
  constructor(
    readonly title: string,
    readonly tickets: number,
    readonly amount: Money,
    readonly online: boolean,
  ) {}
}
