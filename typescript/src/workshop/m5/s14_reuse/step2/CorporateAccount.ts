import type { Money } from '../../../shared/Money.js';
import type { PointsHolder } from './PointsHolder.js';
import { PointsLedger } from './PointsLedger.js';

/**
 * Krok 2 (rozwiązanie): Replace Inheritance with Delegation - konto firmowe ma własny PointsLedger
 * i NIE jest LoyaltyAccount. Nie ma czego blokować wyjątkiem: redeemFreeTicket() po prostu nie istnieje.
 */
export class CorporateAccount implements PointsHolder {
  readonly #company: string;
  readonly #ledger = new PointsLedger();

  constructor(company: string) {
    this.#company = company;
  }

  earn(paidForTickets: Money): void {
    this.#ledger.earn(paidForTickets);
  }

  owner(): string {
    return this.#company;
  }

  points(): number {
    return this.#ledger.points();
  }
}
