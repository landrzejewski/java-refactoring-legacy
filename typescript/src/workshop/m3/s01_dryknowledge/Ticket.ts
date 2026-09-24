import type { LocalTime } from '../../shared/time.js';

/**
 * Stabilny kontrakt sceny - bilet do sprzedaży lub zwrotu.
 *
 * @param format 2D, 3D albo IMAX
 * @param type   NORMAL, STUDENT, SENIOR albo CHILD
 * @param start  godzina rozpoczęcia seansu (przed 12:00 - seans poranny)
 */
export class Ticket {
  constructor(
    readonly format: string,
    readonly type: string,
    readonly start: LocalTime,
  ) {}
}
