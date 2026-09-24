import type { Ticket } from '../Ticket.js';

/**
 * Krok 2: poprawna, mała poprawa Boy Scout - tylko w dotykanej metodzie i bez zmiany kontraktu:
 * Rename (F2), lista linii zamiast konkatenacji, join zamiast ręcznej pętli,
 * Extract Function dla linii telefonu. Sortowanie, wielkość liter i format zostają - to decyzje biznesowe.
 */
export class TicketPrinter {
  print(ticket: Ticket): string {
    return [
      `Film: ${ticket.title}`,
      `Seans: ${ticket.start.toLocalDate().toString()} ${ticket.start.toLocalTime().toString()}`,
      `Miejsca: ${ticket.seats.join(', ')}`,
      `Klient: ${ticket.email.trim()}`,
      TicketPrinter.phoneLine(ticket),
      `Do zaplaty: ${ticket.total.toFixed(2)}`,
    ].map((line) => line + '\n').join('');
  }

  private static phoneLine(ticket: Ticket): string {
    return 'Tel: ' + (ticket.phone != null ? ticket.phone : '-');
  }
}
