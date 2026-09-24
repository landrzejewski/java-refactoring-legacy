import type { LocalDateTime } from '../../shared/time.js';

/**
 * Stabilny kontrakt sceny: wystawiony bilet.
 *
 * @param holdUntil do kiedy rezerwacja czeka na płatność (15 minut od wystawienia)
 */
export class Ticket {
  constructor(
    readonly code: string,
    readonly label: string,
    readonly issuedAt: LocalDateTime,
    readonly holdUntil: LocalDateTime,
  ) {}
}
