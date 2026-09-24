/** Krok 1: bez zmian. */
export class Hall {
  readonly #name: string;
  readonly #rows: number;
  readonly #seatsPerRow: number;
  readonly #vipFromRow: number;

  constructor(name: string, rows: number, seatsPerRow: number, vipFromRow: number) {
    this.#name = name;
    this.#rows = rows;
    this.#seatsPerRow = seatsPerRow;
    this.#vipFromRow = vipFromRow;
  }

  name(): string {
    return this.#name;
  }

  capacity(): number {
    return this.#rows * this.#seatsPerRow;
  }

  isVip(row: number): boolean {
    return row >= this.#vipFromRow;
  }

  describe(): string {
    return this.#name + ': ' + this.capacity() + ' miejsc, VIP od rzędu ' + this.#vipFromRow;
  }
}
