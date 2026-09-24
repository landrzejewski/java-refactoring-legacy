/**
 * Start: konstruktor bazy woła metodę nadpisywalną `describe()`, żeby zapamiętać etykietę.
 * Override w podklasie wykona się, ZANIM podklasa zainicjalizuje swoje pola (w JS pola podklasy
 * powstają dopiero po powrocie z super(...)).
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
