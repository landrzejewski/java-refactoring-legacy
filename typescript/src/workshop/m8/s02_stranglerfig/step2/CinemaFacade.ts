import type { BookingLedger } from '../BookingLedger.js';
import type { CinemaApi } from '../CinemaApi.js';
import { BookingModule } from './BookingModule.js';
import { LegacyCinema } from './LegacyCinema.js';

/**
 * Krok 2: przejęcie pierwszej ścieżki - rezerwacje obsługuje nowy BookingModule,
 * raport nadal legacy. Oba czytają i piszą tę samą bazę, więc raport widzi nowe rezerwacje.
 */
export class CinemaFacade implements CinemaApi {
  private readonly legacy: LegacyCinema;
  private readonly bookings: BookingModule;

  constructor(ledger: BookingLedger) {
    this.legacy = new LegacyCinema(ledger);
    this.bookings = new BookingModule(ledger);
  }

  book(email: string, title: string, format: number, tickets: number, web: boolean): string {
    return this.bookings.book(email, title, format, tickets, web);
  }

  report(): string {
    return this.legacy.report();
  }

  /** Żywa dokumentacja routingu: kto obsługuje którą operację. */
  routes(): ReadonlyMap<string, string> {
    return new Map([['book', 'new'], ['report', 'legacy']]);
  }
}
