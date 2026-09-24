import type { Money } from '../../shared/Money.js';

/** Stabilny kontrakt sceny: rezerwacja zapisywana w bazie. */
export class Booking {
  readonly seats: readonly string[];

  constructor(readonly id: string, readonly email: string, seats: readonly string[], readonly total: Money) {
    this.seats = Object.freeze([...seats]);
  }
}
