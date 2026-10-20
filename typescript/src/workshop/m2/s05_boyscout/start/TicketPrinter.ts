import type { Ticket } from '../Ticket.js';

/**
 * Start: wydruk biletu, do którego i tak musimy zajrzeć (w tym sprincie dochodzi linia "Sala").
 * Nazwy s, d, x, ręczne sklejanie listy miejsc, konkatenacja w pętli. Kusi, żeby "posprzątać wszystko".
 */
export class TicketPrinter {
  print(t: Ticket): string {
    let s = '';
    s = s + 'Film: ' + t.title + '\n';
    const d = t.start.toLocalDate().toString() + ' ' + t.start.toLocalTime().toString();
    s = s + 'Seans: ' + d + '\n';
    let x = '';
    for (let i = 0; i < t.seats.length; i++) {
      if (i > 0) {
        x = x + ', ';
      }
      x = x + t.seats[i]!;
    }
    s = s + 'Miejsca: ' + x + '\n';
    s = s + 'Klient: ' + t.email.trim() + '\n';
    if (t.phone != null) {
      s = s + 'Tel: ' + t.phone + '\n';
    } else {
      s = s + 'Tel: -\n';
    }
    s = s + 'Do zaplaty: ' + t.total.toFixed(2) + '\n';
    return s;
  }
}
