import { Ticket } from './Ticket.js';

/** Krok 2: bez zmian. */
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
