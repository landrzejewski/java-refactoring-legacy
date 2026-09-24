import type { Money } from '../../../shared/Money.js';

/** Start: konto lojalnościowe klienta - 1 pkt za pełne 10.00, 100 pkt = darmowy bilet 2D. */
export class LoyaltyAccount {
  readonly #owner: string;
  #points = 0;

  constructor(owner: string) {
    this.#owner = owner;
  }

  earn(paidForTickets: Money): void {
    this.#points += paidForTickets.amount.dividedToIntegerBy(10).toNumber();
  }

  redeemFreeTicket(): boolean {
    if (this.#points < 100) {
      return false;
    }
    this.#points -= 100;
    return true;
  }

  owner(): string {
    return this.#owner;
  }

  points(): number {
    return this.#points;
  }
}
