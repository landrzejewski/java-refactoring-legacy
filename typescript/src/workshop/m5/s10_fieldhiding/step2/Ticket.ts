/**
 * Krok 2 (rozwiązanie): category() jako metoda instancji - nadpisywalna i wybierana dynamicznie.
 * Teraz label() daje ten sam wynik bez względu na typ referencji.
 */
export class Ticket {
  readonly #type: string;

  constructor(type = 'NORMAL') {
    this.#type = type;
  }

  type(): string {
    return this.#type;
  }

  category(): string {
    return 'BILET';
  }

  label(): string {
    return this.category() + ': ' + this.#type;
  }
}
