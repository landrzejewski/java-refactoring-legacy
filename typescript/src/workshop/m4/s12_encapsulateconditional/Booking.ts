import type { Money } from '../../shared/Money.js';
import type { LocalDateTime } from '../../shared/time.js';

/**
 * Stabilny kontrakt sceny.
 *
 * @param status  NEW, PAID, USED, EXPIRED, CANCELLED
 * @param tickets zapłacone za bilety (bez opłat rezerwacyjnych - te nie podlegają zwrotowi)
 * @param promo   kod promocji albo `null`; kody "FREE..." to bilety darmowe (bez zwrotu)
 */
export class Booking {
  constructor(
    readonly status: string,
    readonly screeningStart: LocalDateTime,
    readonly tickets: Money,
    readonly promo: string | null,
  ) {}
}
