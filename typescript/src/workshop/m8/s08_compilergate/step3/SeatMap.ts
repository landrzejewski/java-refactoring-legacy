/** Krok 3 (bez zmian): mapa miejsc na typach generycznych. */
export class SeatMap {
  private readonly seatsByRow = new Map<number, string[]>();

  take(seat: string): void {
    const row = Number.parseInt(seat.substring(1), 10);
    const seats = this.seatsByRow.get(row) ?? [];
    this.seatsByRow.set(row, seats);
    seats.push(seat);
  }

  takenPerRow(): Map<number, number> {
    return new Map([...this.seatsByRow]
      .sort(([a], [b]) => a - b)
      .map(([row, seats]) => [row, seats.length]));
  }
}
