/**
 * Krok 1: Encapsulate Field - pole prywatne (`#status`), trywialne akcesory get/set na TYM SAMYM polu.
 * Zachowanie bez zmian (setter przyjmuje wszystko), ale każdy zapis przechodzi teraz przez nas.
 */
export class Reservation {
  #status = 'NEW';

  get status(): string {
    return this.#status;
  }

  set status(status: string) {
    this.#status = status;
  }
}
