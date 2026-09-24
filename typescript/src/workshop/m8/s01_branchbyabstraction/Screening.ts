import type { LocalDateTime } from '../../shared/time.js';

/**
 * Stabilny kontrakt sceny: seans w kodach legacy.
 *
 * format: 1 = 2D, 2 = 3D, 3 = IMAX (jak w CinemaManager)
 * vipFromRow: pierwszy rząd VIP w sali
 */
export class Screening {
  constructor(
    readonly title: string,
    readonly format: number,
    readonly start: LocalDateTime,
    readonly vipFromRow: number,
  ) {}
}
