import type { BookingAttempt } from '../BookingAttempt.js';

/**
 * Krok 1: Extract Method - decyzja (cały grot) wydzielona do decide(), a efekt uboczny
 * (audyt) zostaje w book(). Teraz wczesne return w decide() nie ominą audytu.
 */
export class BookingGate {
  private readonly auditLog: string[] = [];

  book(attempt: BookingAttempt): string {
    const result = this.decide(attempt);
    this.auditLog.push(`${attempt.email} -> ${result}`);
    return result;
  }

  private decide(attempt: BookingAttempt): string {
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
    return result;
  }

  audit(): readonly string[] {
    return Object.freeze([...this.auditLog]);
  }
}
