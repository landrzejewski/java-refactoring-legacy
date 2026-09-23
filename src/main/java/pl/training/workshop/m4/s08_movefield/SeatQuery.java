package pl.training.workshop.m4.s08_movefield;

/**
 * Stabilne wejście testu - każdy wariant buduje z niego własne Hall i Screening.
 *
 * @param format legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX
 */
public record SeatQuery(String hall, int vipFromRow, int format, int row) {
}
