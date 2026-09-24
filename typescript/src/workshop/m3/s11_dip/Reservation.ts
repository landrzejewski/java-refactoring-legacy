import type { LocalDateTime } from '../../shared/time.js';

/** Stabilny kontrakt sceny - rezerwacja do potwierdzenia. */
export class Reservation {
  constructor(
    readonly email: string,
    readonly title: string,
    readonly start: LocalDateTime,
    readonly seats: number,
  ) {}
}
