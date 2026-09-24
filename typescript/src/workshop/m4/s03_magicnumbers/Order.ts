import type { LocalTime } from '../../shared/time.js';
import type { Ticket } from './Ticket.js';

/**
 * Stabilny kontrakt sceny.
 *
 * @param format legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX
 * @param online zamówienie przez internet (opłata rezerwacyjna za bilet)
 */
export class Order {
  constructor(
    readonly format: number,
    readonly start: LocalTime,
    readonly online: boolean,
    readonly tickets: readonly Ticket[],
  ) {}
}
