import { type Clock, LocalDateTime, systemClock } from '../../../shared/time.js';
import type { Booking } from '../Booking.js';

/**
 * Krok 1: Parameterize Constructor - wstrzykujemy {@link Clock}, żeby test charakterystyki
 * mógł porównać CAŁY dokument. Konstruktor bez argumentu zostaje z zegarem systemowym,
 * więc dotychczasowi klienci nie widzą różnicy. Reszta kodu - bajt w bajt jak w start.
 */
export class BookingConfirmation {
  constructor(private readonly clock: Clock = systemClock) {}

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
      + 'Wygenerowano: ' + LocalDateTime.now(this.clock).toString() + '\n';
  }
}

// Odpowiednik String.format("%.2f", ...) bez Locale - separator zależy od domyślnego locale.
const AMOUNT: Intl.NumberFormatOptions = { minimumFractionDigits: 2, maximumFractionDigits: 2, useGrouping: false };
