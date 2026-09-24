import { Ticket } from './Ticket.js';

/** Krok 1: bez zmian - miejsce nadal ustawiane setterem. */
export class StandardTicket extends Ticket {
  #seat: string | undefined;

  setSeat(seat: string): void {
    this.#seat = seat;
  }

  seat(): string | undefined {
    return this.#seat;
  }

  override describe(): string {
    return 'NORMAL ' + this.#seat;
  }
}
