/**
 * Krok 2 (rozwiązanie): konstruktor nie woła już metody nadpisywalnej. Pole #label usunięte
 * (Replace Field with Query) - etykieta liczona na żądanie, gdy obiekt jest w pełni zbudowany.
 */
export class Ticket {
  readonly #seat: string;

  constructor(seat: string) {
    this.#seat = seat;
  }

  protected describe(): string {
    return 'Miejsce ' + this.#seat;
  }

  label(): string {
    return this.describe();
  }
}
