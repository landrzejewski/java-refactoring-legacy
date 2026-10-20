import type { Decimal } from 'decimal.js';

export class Memento {
  // Java: package-private constructor and accessor (only Account uses them)
  constructor(private readonly balanceValue: Decimal) {}

  balance(): Decimal {
    return this.balanceValue;
  }
}
