import { Ticket } from './Ticket.js';

/** Krok 3: #seat w bazie; #studentId ma inne znaczenie, więc zostaje w podklasie. */
export class StudentTicket extends Ticket {
  readonly #studentId: string | null;

  constructor(seat: string, studentId: string | null) {
    super(seat);
    this.#studentId = studentId;
  }

  override describe(): string {
    return 'STUDENT ' + this.seat() + ' (legitymacja ' + this.#studentId + ')';
  }
}
