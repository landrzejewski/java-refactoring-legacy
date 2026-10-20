/** Krok 1: dane wejściowe przypadku użycia - prosty rekord, bez Map z HTTP. */
export class BookSeatsCommand {
  readonly rows: readonly number[];

  constructor(readonly email: string, readonly format: string, rows: readonly number[]) {
    this.rows = Object.freeze([...rows]);
  }
}
