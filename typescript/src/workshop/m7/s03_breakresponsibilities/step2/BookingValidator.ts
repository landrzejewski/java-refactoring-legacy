import type { BookingRequest } from '../BookingRequest.js';

/**
 * Krok 1: Extract Class - reguły walidacji mają własnego właściciela (bez zmian w kroku 2).
 * Eksportowany tylko na potrzeby BookingDesk (w Javie klasa pakietowa).
 */
export class BookingValidator {
  firstError(request: BookingRequest): string | undefined {
    if (request.email === null || !request.email.includes('@')) {
      return 'ERROR: niepoprawny e-mail';
    }
    if (request.seats.length === 0) {
      return 'ERROR: brak miejsc';
    }
    for (const seat of request.seats) {
      if (!/^[A-L][0-9]{1,2}$/.test(seat)) {
        return `ERROR: niepoprawne miejsce ${seat}`;
      }
    }
    return undefined;
  }
}
