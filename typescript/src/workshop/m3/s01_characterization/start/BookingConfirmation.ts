import { LocalDateTime } from '../../../shared/time.js';
import type { Booking } from '../Booking.js';

/**
 * Start: nieprzetestowany generator potwierdzenia rezerwacji z systemu kasowego.
 * Nikt nie pamięta wszystkich reguł, a dokument czytają klienci i infolinia.
 * Zanim cokolwiek zmienimy, zapisujemy test charakterystyki: co kod ROBI, a nie co POWINIEN.
 * Przeszkody: bieżący czas w dokumencie i formatowanie zależne od domyślnego locale.
 */
export class BookingConfirmation {
  confirm(b: Booking): string {
    let sum = 0;
    for (const t of b.ticketTypes) {
      let p: number;
      if (b.format === 3) {
        p = 40.00;
      } else if (b.format === 2) {
        p = 32.00;
      } else {
        p = 25.00;
      }
      if (t === 'S') {
        p = p * 0.75;
      } else if (t === 'E') {
        p = p * 0.70;
      } else if (t === 'C') {
        p = p * 0.60;
      }
      if (b.start.hour < 12) {
        p = p - 5.00;
      }
      sum = sum + p;
    }
    if (b.ticketTypes.length > 10) {
      sum = sum * 0.9;
    }
    const fee = b.online ? 2.00 * b.ticketTypes.length : 0;
    const f = b.format === 3 ? 'IMAX' : b.format === 2 ? '3D' : '2D';
    return 'POTWIERDZENIE REZERWACJI\n'
      + 'Klient: ' + b.customer.trim().toUpperCase() + '\n'
      + 'Film: ' + b.title + ', ' + f + ', ' + b.start.toString() + '\n'
      + 'Bilety: ' + b.ticketTypes.length + ' [' + b.ticketTypes.join(', ') + ']\n'
      + 'Bilety razem: ' + sum.toLocaleString(undefined, AMOUNT) + '\n'
      + 'Oplata rezerwacyjna: ' + fee.toLocaleString(undefined, AMOUNT) + '\n'
      + 'Do zaplaty: ' + (sum + fee).toLocaleString(undefined, AMOUNT) + '\n'
      + 'Wygenerowano: ' + LocalDateTime.now().toString() + '\n';
  }
}

// Odpowiednik String.format("%.2f", ...) bez Locale - separator zależy od domyślnego locale.
const AMOUNT: Intl.NumberFormatOptions = { minimumFractionDigits: 2, maximumFractionDigits: 2, useGrouping: false };
