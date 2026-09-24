import type { LocalTime } from '../../shared/time.js';

/**
 * Stabilny kontrakt sceny.
 *
 * @param format     legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX
 * @param type       legacy kod biletu: "N", "S" (student), "E" (senior), "C" (dziecko)
 * @param row        rząd miejsca albo `null` dla wolnej widowni (bez numerowanych miejsc)
 * @param ownGlasses klient ma własne okulary 3D
 */
export class TicketRequest {
  constructor(
    readonly format: number,
    readonly type: string,
    readonly start: LocalTime,
    readonly row: number | null,
    readonly ownGlasses: boolean,
  ) {}
}
