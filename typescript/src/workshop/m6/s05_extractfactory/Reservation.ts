import type { Money } from '../../shared/Money.js';
import type { LocalDateTime } from '../../shared/time.js';

/**
 * Stabilny kontrakt sceny: utworzona rezerwacja. Kasa płaci od razu, więc expiresAt === null.
 */
export class Reservation {
  constructor(
    readonly id: string,
    readonly channel: string,
    readonly email: string,
    readonly seats: readonly string[],
    readonly fee: Money,
    readonly expiresAt: LocalDateTime | null,
  ) {}
}
