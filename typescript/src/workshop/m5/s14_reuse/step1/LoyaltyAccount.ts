import type { Money } from '../../../shared/Money.js';
import { PointsLedger } from './PointsLedger.js';

/** Krok 1: konto deleguje arytmetykę punktów do PointsLedger; publiczne API bez zmian. */
export class LoyaltyAccount {
  readonly #owner: string;
  readonly #ledger = new PointsLedger();

  constructor(owner: string) {
    this.#owner = owner;
  }

  earn(paidForTickets: Money): void {
    this.#ledger.earn(paidForTickets);
  }

  redeemFreeTicket(): boolean {
    return this.#ledger.spend(100);
  }

  owner(): string {
    return this.#owner;
  }

  points(): number {
    return this.#ledger.points();
  }
}
