import type { BookingLedger } from '../BookingLedger.js';
import type { CinemaApi } from '../CinemaApi.js';
import { BookingModule } from './BookingModule.js';
import { ReportModule } from './ReportModule.js';

/**
 * Krok 4: usunięcie legacy - po zamknięciu okna wycofania LegacyCinema znika (Safe Delete).
 * Fasada zostaje jako granica API; można ją uprościć, gdy routing przestanie być potrzebny.
 */
export class CinemaFacade implements CinemaApi {
  private readonly bookings: BookingModule;
  private readonly reports: ReportModule;

  constructor(ledger: BookingLedger) {
    this.bookings = new BookingModule(ledger);
    this.reports = new ReportModule(ledger);
  }

  book(email: string, title: string, format: number, tickets: number, web: boolean): string {
    return this.bookings.book(email, title, format, tickets, web);
  }

  report(): string {
    return this.reports.report();
  }

  /** Żywa dokumentacja routingu: kto obsługuje którą operację. */
  routes(): ReadonlyMap<string, string> {
    return new Map([['book', 'new'], ['report', 'new']]);
  }
}
