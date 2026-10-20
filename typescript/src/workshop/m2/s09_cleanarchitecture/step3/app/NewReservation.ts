import type { Money } from '../../../../shared/Money.js';

/** Krok 2: dane przekraczające granicę do portu zapisu - rekord, nie unknown[]. */
export class NewReservation {
  constructor(
    readonly email: string,
    readonly format: string,
    readonly seats: number,
    readonly total: Money,
  ) {}
}
