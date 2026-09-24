import type { BookingAttempt } from '../BookingAttempt.js';

/**
 * Start: grot strzały - główna ścieżka (BOOKED) schowana na piątym poziomie zagnieżdżenia,
 * wynik zbierany w zmiennej result. Uwaga: wpis do audytu na końcu dotyczy KAŻDEJ ścieżki.
 */
export class BookingGate {
  private readonly auditLog: string[] = [];

  book(attempt: BookingAttempt): string {
    let result: string;
    if (attempt.screeningFound) {
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
    } else {
      result = 'NO_SCREENING';
    }
    this.auditLog.push(`${attempt.email} -> ${result}`);
    return result;
  }

  audit(): readonly string[] {
    return Object.freeze([...this.auditLog]);
  }
}
