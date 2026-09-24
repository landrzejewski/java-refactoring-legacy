/**
 * Krok 1: baza bez zmian - naprawa lokalna w podklasie.
 */
export class Ticket {
  readonly #seat: string;
  readonly #label: string;

  constructor(seat: string) {
    this.#seat = seat;
    this.#label = this.describe();
  }

  protected describe(): string {
    return 'Miejsce ' + this.#seat;
  }

  label(): string {
    return this.#label;
  }
}
