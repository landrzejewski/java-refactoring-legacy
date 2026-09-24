import type { Screening } from './Screening.js';

/**
 * Rezerwacja jednego miejsca.
 *
 * @param seat numer miejsca (numer z sali, nie pozycja na liście wolnych miejsc)
 */
export class Booking {
  constructor(readonly id: string, readonly screening: Screening, readonly seat: number) {}
}
