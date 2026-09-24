import { Ticket } from './Ticket.js';

/** Start: miejsce ustawiane setterem po utworzeniu - pole mutowalne. */
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
