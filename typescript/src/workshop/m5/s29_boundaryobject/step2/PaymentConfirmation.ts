import { IllegalArgumentError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';

/** Wynik autoryzacji w języku kina (w Javie: enum zagnieżdżony w rekordzie). */
export enum Outcome {
  APPROVED = 'APPROVED',
  DECLINED = 'DECLINED',
  ERROR = 'ERROR',
}

/**
 * Krok 2: bez zmian - Boundary Object - odpowiedź terminala jako niezmienna wartość w języku kina.
 * Walidacja w konstruktorze: obiekt, który istnieje, jest poprawny, więc dalszy kod nie musi
 * niczego sprawdzać ani zgadywać. Kwota jako Money, wynik jako enum, kod nigdy pusty.
 */
export class PaymentConfirmation {
  readonly outcome: Outcome;
  readonly code: string;
  readonly authorized: Money;

  constructor(outcome: Outcome, code: string, authorized: Money) {
    this.outcome = requireNonNull(outcome, 'outcome');
    this.authorized = requireNonNull(authorized, 'authorized');
    if (code === null || code === undefined || code.trim() === '') {
      throw new IllegalArgumentError('code must not be blank');
    }
    this.code = code;
    if (authorized.compareTo(Money.ZERO) < 0) {
      throw new IllegalArgumentError('authorized amount must not be negative');
    }
    if (outcome !== Outcome.APPROVED && authorized.isGreaterThan(Money.ZERO)) {
      throw new IllegalArgumentError('only approved payment can authorize money');
    }
  }

  approved(): boolean {
    return this.outcome === Outcome.APPROVED;
  }

  /** Autoryzacja częściowa: terminal zgodził się na mniej, niż prosiliśmy. */
  isPartialFor(requested: Money): boolean {
    return this.approved() && this.authorized.compareTo(requested) < 0;
  }
}
