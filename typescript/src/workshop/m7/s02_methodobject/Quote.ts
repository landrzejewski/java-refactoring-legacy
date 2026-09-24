import type { Money } from '../../shared/Money.js';

/** Stabilny kontrakt sceny: wycena zamówienia. */
export class Quote {
  constructor(
    readonly tickets: Money,
    readonly fees: Money,
    readonly total: Money,
    readonly loyaltyPoints: number,
  ) {}

  toString(): string {
    return `Quote[tickets=${this.tickets.toString()}, fees=${this.fees.toString()}, `
      + `total=${this.total.toString()}, loyaltyPoints=${this.loyaltyPoints}]`;
  }
}
