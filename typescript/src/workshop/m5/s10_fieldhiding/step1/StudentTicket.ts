import { Ticket } from './Ticket.js';

/** Krok 1: ukrywające pole usunięte; ukrywająca metoda statyczna jeszcze zostaje. */
export class StudentTicket extends Ticket {
  constructor() {
    super('STUDENT');
  }

  static override category(): string {
    return 'BILET ULGOWY';
  }
}
