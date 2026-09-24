import type { LocalTime } from '../../shared/time.js';

/**
 * Stabilny kontrakt sceny: zamówienie (także grupowe) na jeden seans.
 *
 * format      - 2D, 3D albo IMAX
 * ticketTypes - NORMAL, STUDENT, SENIOR albo CHILD - jeden wpis na bilet
 * vipSeats    - ile z tych biletów to miejsca VIP
 */
export class GroupOrder {
  constructor(
    readonly format: string,
    readonly start: LocalTime,
    readonly ticketTypes: readonly string[],
    readonly vipSeats: number,
    readonly ownGlasses: boolean,
    readonly online: boolean,
  ) {}
}
