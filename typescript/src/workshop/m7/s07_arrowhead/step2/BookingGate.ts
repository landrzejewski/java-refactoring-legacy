import type { BookingAttempt } from '../BookingAttempt.js';

/**
 * Krok 2: odwrócenie najbardziej zewnętrznego warunku - pierwsza guard clause
 * (brak seansu) i usunięcie jednego poziomu else. Test po każdym poziomie.
 */
export class BookingGate {
  private readonly auditLog: string[] = [];

  book(attempt: BookingAttempt): string {
    const result = this.decide(attempt);
    this.auditLog.push(`${attempt.email} -> ${result}`);
    return result;
  }

  private decide(attempt: BookingAttempt): string {
    if (!attempt.screeningFound) {
      return 'NO_SCREENING';
    }
    let result: string;
    if (attempt.salesOpen) {
      if (!attempt.customerBlocked) {
        if (attempt.requestedSeats > 0) {
          if (attempt.requestedSeats <= attempt.freeSeats) {
            result = 'BOOKED';
          } else {
            result = 'SOLD_OUT';
          }
        } else {
          result = 'NO_SEATS_REQUESTED';
        }
      } else {
        result = 'CUSTOMER_BLOCKED';
      }
    } else {
      result = 'SALES_CLOSED';
    }
    return result;
  }

  audit(): readonly string[] {
    return Object.freeze([...this.auditLog]);
  }
}
