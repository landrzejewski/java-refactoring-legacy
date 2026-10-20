import type { LocalDateTime } from '../../shared/time.js';

/** Stabilny kontrakt sceny: szkic rezerwacji do walidacji (now podawane jawnie - testowalność). */
export class ReservationDraft {
  constructor(
    readonly email: string | null,
    readonly seats: readonly string[],
    readonly showStart: LocalDateTime,
    readonly now: LocalDateTime,
  ) {}
}
