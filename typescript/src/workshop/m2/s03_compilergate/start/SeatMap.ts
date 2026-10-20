/**
 * Start: mapa zajętych miejsc na "surowym" typie - new Map() bez argumentów typu to Map<any, any>
 * (rawtypes) - i niesprawdzanych asercjach typu na wartościach any (unchecked).
 */
export class SeatMap {
  private readonly seatsByRow = new Map();

  take(seat: string): void {
    const row = Number.parseInt(seat.substring(1), 10);
    let seats = this.seatsByRow.get(row) as string[] | undefined;
    if (seats === undefined) {
      seats = [];
      this.seatsByRow.set(row, seats);
    }
    seats.push(seat);
  }

  takenPerRow(): Map<number, number> {
    const result = new Map<number, number>();
    for (const row of [...this.seatsByRow.keys()].sort((a, b) => a - b)) {
      result.set(row as number, (this.seatsByRow.get(row) as string[]).length);
    }
    return result;
  }
}
