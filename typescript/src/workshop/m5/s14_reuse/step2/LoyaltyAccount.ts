import type { Money } from '../../../shared/Money.js';
import type { PointsHolder } from './PointsHolder.js';
import { PointsLedger } from './PointsLedger.js';

/** Krok 2: konto klienta implementuje rolę PointsHolder i nadal wymienia punkty na bilety. */
export class LoyaltyAccount implements PointsHolder {
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
