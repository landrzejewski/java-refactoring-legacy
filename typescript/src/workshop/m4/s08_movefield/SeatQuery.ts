/**
 * Stabilne wejście testu - każdy wariant buduje z niego własne Hall i Screening.
 *
 * @param format legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX
 */
export class SeatQuery {
  constructor(
    readonly hall: string,
    readonly vipFromRow: number,
    readonly format: number,
    readonly row: number,
  ) {}
}
