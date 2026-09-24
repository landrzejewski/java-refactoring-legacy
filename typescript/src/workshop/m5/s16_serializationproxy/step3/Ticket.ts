/**
 * Krok 3: bez zmian.
 */
export abstract class Ticket {
  readonly #title: string;
  readonly #seat: string;

  protected constructor(title: string, seat: string) {
    this.#title = title;
    this.#seat = seat;
  }

  title(): string {
    return this.#title;
  }

  seat(): string {
    return this.#seat;
  }
}
