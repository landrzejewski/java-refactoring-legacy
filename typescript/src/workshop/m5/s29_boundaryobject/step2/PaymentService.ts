import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { LegacyTerminalClient } from '../LegacyTerminalClient.js';
import type { PaymentConfirmation } from './PaymentConfirmation.js';
import { TerminalResponseMapper } from './TerminalResponseMapper.js';

/**
 * Krok 2: serwis nie zna już formatu terminala - grosze i tekst odpowiedzi tłumaczy mapper.
 * Nadal jednak sam woła LegacyTerminalClient i składa wywołanie z mapperem; to zostanie
 * schowane za portem w kroku 3.
 */
export class PaymentService {
  private readonly terminal: LegacyTerminalClient;
  private readonly mapper = new TerminalResponseMapper();
  private readonly confirmations = new Map<string, PaymentConfirmation>();

  constructor(terminal: LegacyTerminalClient) {
    this.terminal = requireNonNull(terminal, 'terminal');
  }

  pay(bookingId: string, amount: Money): string {
    const raw = this.terminal.authorize(bookingId, this.mapper.toGrosze(amount));
    const confirmation = this.mapper.toConfirmation(raw);
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
