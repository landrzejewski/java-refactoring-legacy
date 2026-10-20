import type { Money } from '../../shared/Money.js';
import { BookingStatus } from './BookingStatus.js';

/** Stabilny kontrakt sceny: rezerwacja - mutowalny stan, który też należy do wektora zachowania. */
export class Booking {
  private currentStatus: BookingStatus;

  constructor(
    readonly id: string,
    readonly email: string,
    readonly amount: Money,
    status: BookingStatus,
  ) {
    this.currentStatus = status;
  }

  get status(): BookingStatus {
    return this.currentStatus;
  }

  markPaid(): void {
    this.currentStatus = BookingStatus.PAID;
  }
}
