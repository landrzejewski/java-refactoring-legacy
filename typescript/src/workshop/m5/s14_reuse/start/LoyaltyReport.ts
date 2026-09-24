import type { LoyaltyAccount } from './LoyaltyAccount.js';

/** Start: raport potrzebuje tylko właściciela i punktów - a przyjmuje całe LoyaltyAccount. */
export class LoyaltyReport {
  line(account: LoyaltyAccount): string {
    return account.owner() + ': ' + account.points() + ' pkt';
  }
}
