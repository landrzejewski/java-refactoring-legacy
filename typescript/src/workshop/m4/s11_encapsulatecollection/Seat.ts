/** Stabilny kontrakt sceny: miejsce (niezmienna wartość - niemodyfikowalna lista wystarczy). */
export class Seat {
  constructor(readonly row: number, readonly number: number) {}

  equals(other: unknown): boolean {
    return other instanceof Seat && this.row === other.row && this.number === other.number;
  }

  toString(): string {
    return `${this.row}/${this.number}`;
  }
}
