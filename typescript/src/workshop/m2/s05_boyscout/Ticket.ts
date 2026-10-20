import type { LocalDateTime } from '../../shared/time.js';

/**
 * Stabilny kontrakt sceny: dane biletu do wydruku (kwota jak w legacy - double).
 *
 * phone: może być null - klient nie podał telefonu
 */
export class Ticket {
  constructor(
    readonly title: string,
    readonly start: LocalDateTime,
    readonly seats: readonly string[],
    readonly email: string,
    readonly phone: string | null,
    readonly total: number,
  ) {}
}
