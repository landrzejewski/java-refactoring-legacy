import { Ticket } from './Ticket.js';

/** Krok 1: Rename (pole #seatCode i akcesor seatCode()) na seat - ta sama nazwa dla tego samego znaczenia. */
export class StudentTicket extends Ticket {
  readonly #seat: string;
  readonly #studentId: string | null;

  constructor(seat: string, studentId: string | null) {
    super();
    this.#seat = seat;
    this.#studentId = studentId;
  }

  seat(): string {
    return this.#seat;
  }

  override describe(): string {
    return 'STUDENT ' + this.#seat + ' (legitymacja ' + this.#studentId + ')';
  }
}
