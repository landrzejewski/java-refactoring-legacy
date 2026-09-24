/** Krok 2: Replace Constructor with Factory Method - Hall.imax(...) przejmuje jedyną wiedzę z ImaxHall. */
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

  /** Sala IMAX: VIP zawsze w dwóch ostatnich rzędach (wiedza przeniesiona z konstruktora ImaxHall). */
  static imax(name: string, rows: number, seatsPerRow: number): Hall {
    return new Hall(name, rows, seatsPerRow, rows - 1);
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
