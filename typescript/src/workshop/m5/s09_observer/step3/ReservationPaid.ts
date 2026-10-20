import type { Money } from '../../../shared/Money.js';

/** Krok 3: bez zmian - zdarzenie - fakt "rezerwacja opłacona" z danymi potrzebnymi odbiorcom. */
export class ReservationPaid {
  constructor(
    readonly reservationId: string,
    readonly email: string,
    readonly phone: string,
    readonly amount: Money,
  ) {}
}
