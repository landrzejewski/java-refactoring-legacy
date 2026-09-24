import type { LocalTime } from '../../shared/time.js';

/** Stabilny kontrakt sceny: seans w repertuarze dnia. */
export class Screening {
  constructor(
    readonly title: string,
    readonly format: string,
    readonly start: LocalTime,
    readonly hall: number,
    readonly cancelled: boolean,
  ) {}
}
