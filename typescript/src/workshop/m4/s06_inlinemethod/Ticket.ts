import type { LocalTime } from '../../shared/time.js';

/**
 * Stabilny kontrakt sceny.
 *
 * @param format legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX
 */
export class Ticket {
  constructor(readonly format: number, readonly start: LocalTime) {}
}
