import { UnsupportedOperationError } from '../../../../shared/errors.js';
import { LoyaltyAccount } from './LoyaltyAccount.js';

/**
 * Start: konto firmowe dziedziczy tylko po to, by nie pisać drugi raz naliczania punktów.
 * Firma zbiera punkty do rocznego rabatu i NIE wymienia ich na bilety - więc odziedziczoną operację
 * blokuje wyjątkiem. Każdy klient LoyaltyAccount może dostać ten obiekt i wybuchnąć.
 */
export class CorporateAccount extends LoyaltyAccount {
  constructor(company: string) {
    super(company);
  }

  override redeemFreeTicket(): boolean {
    throw new UnsupportedOperationError('konto firmowe nie wymienia punktów na bilety');
  }
}
