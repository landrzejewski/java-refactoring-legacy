import { Decimal } from 'decimal.js';

import { requireNonNull } from '../../shared/requireNonNull.js';

// Kwota w złotych, zawsze ze skalą 2 i zaokrągleniem HALF_UP.
// Wspólny, nierefaktoryzowany typ wartości warsztatu CineLegacy.
export class Money {
  static readonly ZERO = Money.of('0.00');

  readonly amount: Decimal;

  constructor(amount: Decimal) {
    this.amount = requireNonNull(amount, 'amount').toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }

  static of(amount: string | number): Money {
    return new Money(new Decimal(amount));
  }

  plus(other: Money): Money {
    return new Money(this.amount.plus(other.amount));
  }

  minus(other: Money): Money {
    return new Money(this.amount.minus(other.amount));
  }

  times(factor: number): Money {
    return new Money(this.amount.times(factor));
  }

  // Procent kwoty, np. percent(25) to 25% tej kwoty.
  percent(percent: number): Money {
    return new Money(this.amount.times(percent).dividedBy(100));
  }

  max(other: Money): Money {
    return this.compareTo(other) >= 0 ? this : other;
  }

  isGreaterThan(other: Money): boolean {
    return this.compareTo(other) > 0;
  }

  compareTo(other: Money): number {
    return this.amount.comparedTo(other.amount);
  }

  equals(other: unknown): boolean {
    return other instanceof Money && this.amount.equals(other.amount);
  }

  toString(): string {
    return this.amount.toFixed(2);
  }
}
