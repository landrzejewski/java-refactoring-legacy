import type { LoyaltyProgram } from '../LoyaltyProgram.js';
import type { PaymentListener } from './PaymentListener.js';
import type { ReservationPaid } from './ReservationPaid.js';

/** Krok 3: obserwator - 1 punkt za każde pełne 10.00. */
export class LoyaltyPoints implements PaymentListener {
  constructor(readonly loyalty: LoyaltyProgram) {}

  onPaid(event: ReservationPaid): void {
    this.loyalty.addPoints(event.email, Math.trunc(event.amount.amount.trunc().toNumber() / 10));
  }
}
