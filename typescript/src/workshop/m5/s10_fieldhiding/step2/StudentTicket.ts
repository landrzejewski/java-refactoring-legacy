import { Ticket } from './Ticket.js';

/** Krok 2: prawdziwy override metody instancji - słowo override pilnuje, że coś nadpisujemy. */
export class StudentTicket extends Ticket {
  constructor() {
    super('STUDENT');
  }

  override category(): string {
    return 'BILET ULGOWY';
  }
}
