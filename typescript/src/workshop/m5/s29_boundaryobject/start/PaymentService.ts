import { Decimal } from 'decimal.js';

import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { LegacyTerminalClient } from '../LegacyTerminalClient.js';

/**
 * Start: surowa odpowiedź terminala "OK;AUTH-R1;4500" krąży po serwisie. Każda metoda sama ją
 * rozbiera (split, startsWith, lastIndexOf), sama zgaduje znaczenie pól i sama przelicza grosze.
 * Format obcego systemu przecieka do logiki, a błędna odpowiedź wybucha daleko od granicy.
 */
export class PaymentService {
  private readonly terminal: LegacyTerminalClient;
  private readonly responses = new Map<string, string>();

  constructor(terminal: LegacyTerminalClient) {
    this.terminal = requireNonNull(terminal, 'terminal');
  }

  pay(bookingId: string, amount: Money): string {
    const grosze = amount.amount.times(100).toNumber();
    const raw = this.terminal.authorize(bookingId, grosze);
    this.responses.set(bookingId, raw);
    const parts = raw.split(';');
    if (parts[0] !== 'OK') {
      return `${parts[0]} ${parts[1]}`;
    }
    const authorized = Number.parseInt(parts[2] ?? '', 10);
    if (authorized < grosze) {
      return `PARTIAL ${parts[1]} brakuje ${new Decimal(grosze - authorized).dividedBy(100).toFixed(2)}`;
    }
    return `PAID ${parts[1]}`;
  }

  receipt(bookingId: string): string {
    const raw = this.responses.get(bookingId);
    if (raw === undefined) {
      return `Brak platnosci ${bookingId}`;
    }
    const parts = raw.split(';');
    if (!raw.startsWith('OK')) {
      return `Platnosc ${bookingId} nieudana: ${parts[1]}`;
    }
    return `Platnosc ${bookingId}: ${new Decimal(parts[2] ?? '').dividedBy(100).toFixed(2)} (${parts[1]})`;
  }

  totalAuthorized(): Money {
    let total = Money.ZERO;
    for (const raw of this.responses.values()) {
      if (raw.startsWith('OK')) {
        total = total.plus(new Money(new Decimal(raw.substring(raw.lastIndexOf(';') + 1)).dividedBy(100)));
      }
    }
    return total;
  }
}
