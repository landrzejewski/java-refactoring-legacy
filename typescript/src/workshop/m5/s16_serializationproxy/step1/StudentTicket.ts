import { Ticket } from './Ticket.js';

/**
 * Krok 1: format się zmienił, ale nic tego nie mówi (brak wersji formatu), więc odczyt starych danych
 * NIE rzuci wyjątku - klucze title i seat trafiają na nazwy metod i są pomijane, a _title i _seat
 * po cichu zostają undefined. Brak wersji nie migruje stanu.
 */
export class StudentTicket extends Ticket {
  private readonly studentId: string;

  constructor(title: string, seat: string, studentId: string) {
    super(title, seat);
    this.studentId = studentId;
  }

  describe(): string {
    return this.title() + ' ' + this.seat() + ' (legitymacja ' + this.studentId + ')';
  }
}
