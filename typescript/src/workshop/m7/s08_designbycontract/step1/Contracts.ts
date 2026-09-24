import { IllegalArgumentError, IllegalStateError } from '../../../../shared/errors.js';

/**
 * Jawne kontrole kontraktu - działają zawsze, w przeciwieństwie do console.assert
 * (tylko wypisuje komunikat i nie przerywa działania).
 * require: obowiązek klienta (IllegalArgumentError),
 * ensure: gwarancja operacji (IllegalStateError).
 * Eksportowane tylko na potrzeby SeatPool (w Javie klasa pakietowa).
 */
export class Contracts {
  private constructor() {}

  static require(condition: boolean, message: string): void {
    if (!condition) {
      throw new IllegalArgumentError(message);
    }
  }

  static ensure(condition: boolean, message: string): void {
    if (!condition) {
      throw new IllegalStateError(message);
    }
  }
}
