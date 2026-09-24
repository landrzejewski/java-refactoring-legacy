import { Ticket } from './Ticket.js';

/** Krok 2: bez zmian - normalizacja VIP zostaje. */
export class VipTicket extends Ticket {
  readonly #seat: string;

  constructor(seat: string) {
    super();
    this.#seat = seat.toUpperCase();
  }

  seat(): string {
    return this.#seat;
  }

  override describe(): string {
    return 'VIP ' + this.#seat;
  }
}
