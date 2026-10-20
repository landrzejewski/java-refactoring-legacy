import type { LocalTime } from '../../shared/time.js';

/**
 * Stabilny kontrakt sceny - dane do wyceny miejsca.
 *
 * @param format     2D, 3D albo IMAX
 * @param start      godzina seansu (przed 12:00 - seans poranny, -5.00)
 * @param row        rząd miejsca
 * @param vipFromRow od tego rzędu miejsce jest VIP (+10.00)
 */
export class TicketQuote {
  constructor(
    readonly format: string,
    readonly start: LocalTime,
    readonly row: number,
    readonly vipFromRow: number,
  ) {}
}
