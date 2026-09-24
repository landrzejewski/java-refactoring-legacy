import type { Screening } from './Screening.js';

/**
 * Stabilny kontrakt sceny: żądanie rezerwacji.
 *
 * seats: miejsca w formacie litera + rząd, np. "A10"
 * types: typy biletów legacy: N, S (student), E (senior), C (dziecko)
 */
export class BookingRequest {
  constructor(
    readonly screening: Screening,
    readonly seats: readonly string[],
    readonly types: readonly string[],
    readonly web: boolean,
    readonly ownGlasses: boolean,
  ) {}
}
