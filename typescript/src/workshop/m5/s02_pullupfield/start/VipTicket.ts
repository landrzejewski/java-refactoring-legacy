import { Ticket } from './Ticket.js';

/** Start: miejsce normalizowane do wielkich liter - ta reguła dotyczy tylko VIP. */
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
