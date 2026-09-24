import type { LoyaltyAccount } from './LoyaltyAccount.js';

/** Krok 1: bez zmian. */
export class LoyaltyReport {
  line(account: LoyaltyAccount): string {
    return account.owner() + ': ' + account.points() + ' pkt';
  }
}
