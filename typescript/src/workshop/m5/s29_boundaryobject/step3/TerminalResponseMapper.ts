import { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import { Outcome, PaymentConfirmation } from './PaymentConfirmation.js';

/**
 * Krok 3: mapper na granicy, teraz używany tylko przez adapter. Jedyne miejsce,
 * które zna format terminala: grosze w obie strony i tekst "STATUS;KOD;KWOTA".
 * Niepoprawna odpowiedź jest odrzucana tu, a nie trzy metody dalej.
 */
export class TerminalResponseMapper {
  toGrosze(amount: Money): number {
    return amount.amount.times(100).toNumber();
  }

  toConfirmation(raw: string): PaymentConfirmation {
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
