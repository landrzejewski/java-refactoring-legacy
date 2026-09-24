import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { LocalDate, LocalDateTime } from '../../../shared/time.js';
import type { BookingRepository } from './BookingRepository.js';
import { Formats } from './Formats.js';
import { LegacyDb } from './LegacyDb.js';

/**
 * Krok 4: raporty mają jednego właściciela. Czytają rezerwacje przez BookingRepository
 * (nie przez unknown[]), więc kolejny krok może zmienić magazyn bez dotykania raportów.
 * Kod metod przeniesiony dosłownie - kolejność sumowania number bez zmian.
 * Eksportowany tylko na potrzeby CinemaManager (w Javie klasa pakietowa).
 */
export class ReportService {
  private readonly bookings: BookingRepository;

  constructor(bookings: BookingRepository) {
    this.bookings = requireNonNull(bookings, 'bookings');
  }

  dailyReport(day: LocalDate): string {
    let sb = '';
    sb += 'RAPORT DZIENNY ' + day.toString() + '\n';
    const byTitle = new Map<string, number[]>();
    let fees = 0;
    let tickets = 0;
    for (const b of this.bookings.all()) {
      const status = b.status;
      if (status !== 1 && status !== 2) {
        continue;
      }
      const s = LegacyDb.SCREENINGS.get(b.screeningId)!;
      if (!(s[2] as LocalDateTime).toLocalDate().equals(day)) {
        continue;
      }
      let row = byTitle.get(s[0] as string);
      if (row === undefined) {
        row = [0, 0];
        byTitle.set(s[0] as string, row);
      }
      row[0] = row[0]! + b.seats.length;
      row[1] = row[1]! + b.ticketsSum;
      fees = fees + (b.total - b.ticketsSum);
      tickets = tickets + b.seats.length;
    }
    let revenue = 0;
    // TreeMap w oryginale - tytuły posortowane
    for (const title of [...byTitle.keys()].sort()) {
      const row = byTitle.get(title)!;
      sb += title + ': ' + Math.trunc(row[0]!) + ' bil., ' + Formats.amount(row[1]!) + '\n';
      revenue = revenue + row[1]!;
    }
    sb += 'Biletow: ' + tickets + '\n';
    sb += 'Przychod z biletow: ' + Formats.amount(revenue) + '\n';
    sb += 'Oplaty rezerwacyjne: ' + Formats.amount(fees) + '\n';
    sb += 'Netto (bez VAT 8%): ' + Formats.amount(revenue / 1.08) + '\n';
    return sb;
  }

  settlement(title: string, week: number): string {
    let revenue = 0;
    for (const b of this.bookings.all()) {
      const status = b.status;
      const s = LegacyDb.SCREENINGS.get(b.screeningId)!;
      if ((status === 1 || status === 2) && s[0] === title) {
        revenue = revenue + b.ticketsSum;
      }
    }
    let share: number;
    if (week === 1) {
      share = revenue * 0.50;
    } else if (week === 2) {
      share = revenue * 0.40;
    } else {
      share = revenue * 0.35;
    }
    if (share < 500.00) {
      share = 500.00;
    }
    return 'ROZLICZENIE ' + title + ' tydzien ' + week + ': przychod '
      + Formats.amount(revenue) + ', dla dystrybutora ' + Formats.amount(share);
  }
}
