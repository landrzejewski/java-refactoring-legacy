/**
 * Krok 3 (rozwiązanie): Pull Members Up dla pola `#seat` i akcesora `seat()`.
 * Pole w bazie jest prywatne (#) i readonly, ustawiane przez super(...) - nie surowe protected.
 */
export abstract class Ticket {
  readonly #seat: string;

  protected constructor(seat: string) {
    this.#seat = seat;
  }

  seat(): string {
    return this.#seat;
  }

  abstract describe(): string;
}
