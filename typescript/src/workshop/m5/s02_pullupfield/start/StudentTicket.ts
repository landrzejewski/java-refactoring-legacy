import { Ticket } from './Ticket.js';

/** Start: to samo znaczenie co "seat", ale inna nazwa. studentId to inne pojęcie - zostaje tutaj. */
export class StudentTicket extends Ticket {
  readonly #seatCode: string;
  readonly #studentId: string | null;

  constructor(seatCode: string, studentId: string | null) {
    super();
    this.#seatCode = seatCode;
    this.#studentId = studentId;
  }

  seatCode(): string {
    return this.#seatCode;
  }

  override describe(): string {
    return 'STUDENT ' + this.#seatCode + ' (legitymacja ' + this.#studentId + ')';
  }
}
