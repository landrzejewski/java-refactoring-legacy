/**
 * Krok 2: baza nie jest już częścią formatu danych - formatem zarządza proxy serializacji w podklasie,
 * więc pola mogą być prywatne ES (#), a przyszłe ruchy w hierarchii nie zmienią postaci JSON.
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
