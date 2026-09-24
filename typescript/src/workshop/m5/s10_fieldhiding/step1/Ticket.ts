/**
 * Krok 1: jedno pole zamiast dwóch slotów - prywatne, readonly, ustawiane przez konstruktor
 * (Encapsulate Field + parametr konstruktora). Podklasa przekazuje swoją wartość przez super(...).
 * (W Javie dwa konstruktory: publiczny Ticket() i chroniony Ticket(type); w TS jeden z wartością domyślną.)
 */
export class Ticket {
  readonly #type: string;

  constructor(type = 'NORMAL') {
    this.#type = type;
  }

  type(): string {
    return this.#type;
  }

  static category(): string {
    return 'BILET';
  }

  label(): string {
    return Ticket.category() + ': ' + this.#type;
  }
}
