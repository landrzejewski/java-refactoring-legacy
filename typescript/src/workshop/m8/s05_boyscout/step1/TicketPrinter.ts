import { Decimal } from 'decimal.js';

import { Money } from '../../../shared/Money.js';
import type { Ticket } from '../Ticket.js';

/**
 * Krok 1 (NADUŻYCIE - antyprzykład): "skoro już tu jestem, posprzątam wszystko". Obok
 * dobrych ruchów przemycono trzy zmiany zachowania: sortowanie miejsc, e-mail małymi literami
 * i pominięcie linii "Tel" bez telefonu. Test równoważności to wykrywa - ten krok cofamy.
 */
export class TicketPrinter {
  print(ticket: Ticket): string {
    const lines: string[] = [];
    lines.push(`Film: ${ticket.title}`);
    lines.push(`Seans: ${ticket.start.toLocalDate().toString()} ${ticket.start.toLocalTime().toString()}`);
    const sortedSeats = [...ticket.seats].sort();
    lines.push(`Miejsca: ${sortedSeats.join(', ')}`);
    lines.push(`Klient: ${ticket.email.trim().toLowerCase()}`);
    if (ticket.phone != null) {
      lines.push(`Tel: ${ticket.phone}`);
    }
    lines.push(`Do zaplaty: ${new Money(new Decimal(ticket.total)).toString()}`);
    return lines.map((line) => line + '\n').join('');
  }
}
