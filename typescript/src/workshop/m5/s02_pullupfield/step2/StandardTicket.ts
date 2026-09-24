import { Ticket } from './Ticket.js';

/**
 * Krok 2: ujednolicenie cyklu życia - miejsce przez konstruktor, pole readonly, setter usunięty.
 * Dopiero teraz wszystkie trzy pola "#seat" mają ten sam typ, znaczenie i moment inicjalizacji.
 */
export class StandardTicket extends Ticket {
  readonly #seat: string;

  constructor(seat: string) {
    super();
    this.#seat = seat;
  }

  seat(): string {
    return this.#seat;
  }

  override describe(): string {
    return 'NORMAL ' + this.#seat;
  }
}
