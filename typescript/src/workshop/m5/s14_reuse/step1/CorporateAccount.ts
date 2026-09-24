import { UnsupportedOperationError } from '../../../../shared/errors.js';
import { LoyaltyAccount } from './LoyaltyAccount.js';

/**
 * Krok 1: bez zmian - nadal dziedziczy (i nadal łamie zastępowalność).
 */
export class CorporateAccount extends LoyaltyAccount {
  constructor(company: string) {
    super(company);
  }

  override redeemFreeTicket(): boolean {
    throw new UnsupportedOperationError('konto firmowe nie wymienia punktów na bilety');
  }
}
