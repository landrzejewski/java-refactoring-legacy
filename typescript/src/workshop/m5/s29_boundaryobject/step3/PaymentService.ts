import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { PaymentConfirmation } from './PaymentConfirmation.js';
import type { PaymentTerminal } from './PaymentTerminal.js';

/**
 * Krok 3: serwis zależy tylko od portu PaymentTerminal i widzi wyłącznie PaymentConfirmation.
 * Granica jest kompletna: Adapter (kształt wywołania) + mapper (format danych)
 * + Boundary Object (poprawna wartość w języku domeny).
 */
export class PaymentService {
  private readonly terminal: PaymentTerminal;
  private readonly confirmations = new Map<string, PaymentConfirmation>();

  constructor(terminal: PaymentTerminal) {
    this.terminal = requireNonNull(terminal, 'terminal');
  }

  pay(bookingId: string, amount: Money): string {
    const confirmation = this.terminal.authorize(bookingId, amount);
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
}
