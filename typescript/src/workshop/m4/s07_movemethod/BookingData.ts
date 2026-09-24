import type { LocalDateTime } from '../../shared/time.js';

/**
 * Stabilne wejście testu - z niego każdy wariant buduje własne Screening i Booking
 * (typy te zmieniają się w trakcie sceny, więc mieszkają w katalogach start/stepN).
 */
export class BookingData {
  constructor(
    readonly id: string,
    readonly title: string,
    readonly format: number,
    readonly start: LocalDateTime,
    readonly hall: number,
    readonly freeSeats: readonly number[],
    readonly seat: number,
  ) {}
}
