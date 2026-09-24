import type { BookingLedger } from '../BookingLedger.js';
import type { CinemaApi } from '../CinemaApi.js';
import { LegacyCinema } from './LegacyCinema.js';

/**
 * Krok 1: fasada 1:1 - każde wywołanie trafia do starego systemu. Zachowanie się nie zmienia,
 * ale od teraz mamy jedno miejsce, w którym można przekierować pojedynczą operację.
 */
export class CinemaFacade implements CinemaApi {
  private readonly legacy: LegacyCinema;

  constructor(ledger: BookingLedger) {
    this.legacy = new LegacyCinema(ledger);
  }

  book(email: string, title: string, format: number, tickets: number, web: boolean): string {
    return this.legacy.book(email, title, format, tickets, web);
  }

  report(): string {
    return this.legacy.report();
  }

  /** Żywa dokumentacja routingu: kto obsługuje którą operację. */
  routes(): ReadonlyMap<string, string> {
    return new Map([['book', 'legacy'], ['report', 'legacy']]);
  }
}
