import { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { LegacyTerminalClient } from '../LegacyTerminalClient.js';
import { Outcome, PaymentConfirmation } from './PaymentConfirmation.js';

/**
 * Krok 1: odpowiedź jest rozbierana RAZ, zaraz po wywołaniu terminala, do PaymentConfirmation.
 * Serwis przechowuje i czyta już tylko obiekt graniczny; parsowanie jest jeszcze prywatną
 * metodą serwisu - do przeniesienia w kroku 2.
 */
export class PaymentService {
  private readonly terminal: LegacyTerminalClient;
  private readonly confirmations = new Map<string, PaymentConfirmation>();

  constructor(terminal: LegacyTerminalClient) {
    this.terminal = requireNonNull(terminal, 'terminal');
  }

  pay(bookingId: string, amount: Money): string {
    const grosze = amount.amount.times(100).toNumber();
    const confirmation = PaymentService.parse(this.terminal.authorize(bookingId, grosze));
    this.confirmations.set(bookingId, confirmation);
    if (!confirmation.approved()) {
      return `${confirmation.outcome} ${confirmation.code}`;
    }
    if (confirmation.isPartialFor(amount)) {
      return `PARTIAL ${confirmation.code} brakuje ${amount.minus(confirmation.authorized).toString()}`;
    }
    return `PAID ${confirmation.code}`;
  }

  receipt(bookingId: string): string {
    const confirmation = this.confirmations.get(bookingId);
    if (confirmation === undefined) {
      return `Brak platnosci ${bookingId}`;
    }
    if (!confirmation.approved()) {
      return `Platnosc ${bookingId} nieudana: ${confirmation.code}`;
    }
    return `Platnosc ${bookingId}: ${confirmation.authorized.toString()} (${confirmation.code})`;
  }

  totalAuthorized(): Money {
    let total = Money.ZERO;
    for (const confirmation of this.confirmations.values()) {
      total = total.plus(confirmation.authorized);
    }
    return total;
  }

  private static parse(raw: string): PaymentConfirmation {
    const parts = raw.split(';');
    if (parts.length !== 3) {
      throw new IllegalArgumentError(`malformed terminal response: ${raw}`);
    }
    const [status = '', code = '', grosze = ''] = parts;
    let outcome: Outcome;
    switch (status) {
      case 'OK': outcome = Outcome.APPROVED; break;
      case 'DECLINED': outcome = Outcome.DECLINED; break;
      case 'ERROR': outcome = Outcome.ERROR; break;
      default: throw new IllegalArgumentError(`malformed terminal response: ${raw}`);
    }
    // W Javie new BigDecimal("abc") rzuca NumberFormatException (podklasa IllegalArgumentException);
    // Decimal przyjąłby "NaN" i rzuca własnym błędem, więc kwotę w groszach sprawdzamy wprost.
    if (!/^[+-]?\d+$/.test(grosze)) {
      throw new IllegalArgumentError(`malformed terminal response: ${raw}`);
    }
    return new PaymentConfirmation(outcome, code, new Money(new Decimal(grosze).dividedBy(100)));
  }
}
