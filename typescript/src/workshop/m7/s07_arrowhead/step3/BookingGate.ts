import type { BookingAttempt } from '../BookingAttempt.js';

/**
 * Krok 3 (rozwiązanie): pozostałe poziomy spłaszczone do guard clauses, zmienna result
 * zniknęła. Kolejność warunków identyczna jak w start - priorytet błędów zachowany.
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
    if (!attempt.salesOpen) {
      return 'SALES_CLOSED';
    }
    if (attempt.customerBlocked) {
      return 'CUSTOMER_BLOCKED';
    }
    if (attempt.requestedSeats <= 0) {
      return 'NO_SEATS_REQUESTED';
    }
    if (attempt.requestedSeats > attempt.freeSeats) {
      return 'SOLD_OUT';
    }
    return 'BOOKED';
  }

  audit(): readonly string[] {
    return Object.freeze([...this.auditLog]);
  }
}
