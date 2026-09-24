/**
 * Rola "plan miejsc do odczytu".
 * Kontrakt: `freeSeats()` równa się liczbie miejsc 1..capacity, dla których `isFree`.
 */
export interface SeatMap {
  isFree(seat: number): boolean;

  freeSeats(): number;

  capacity(): number;
}
