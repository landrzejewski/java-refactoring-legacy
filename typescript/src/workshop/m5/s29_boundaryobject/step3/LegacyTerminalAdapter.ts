import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Money } from '../../../shared/Money.js';
import type { LegacyTerminalClient } from '../LegacyTerminalClient.js';
import type { PaymentConfirmation } from './PaymentConfirmation.js';
import type { PaymentTerminal } from './PaymentTerminal.js';
import { TerminalResponseMapper } from './TerminalResponseMapper.js';

/**
 * Krok 3: Adapter implementujący port domeny. Łączy klienta terminala z mapperem;
 * poza tą klasą nikt nie widzi groszy ani tekstu "OK;AUTH;4500".
 */
export class LegacyTerminalAdapter implements PaymentTerminal {
  private readonly client: LegacyTerminalClient;
  private readonly mapper = new TerminalResponseMapper();

  constructor(client: LegacyTerminalClient) {
    this.client = requireNonNull(client, 'client');
  }

  authorize(bookingId: string, amount: Money): PaymentConfirmation {
    return this.mapper.toConfirmation(this.client.authorize(bookingId, this.mapper.toGrosze(amount)));
  }
}
