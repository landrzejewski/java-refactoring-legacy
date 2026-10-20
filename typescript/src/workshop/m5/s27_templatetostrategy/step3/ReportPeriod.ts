import type { LocalDate } from '../../../shared/time.js';

/** Krok 3: Strategy (bez zmian od kroku 2) dla osi "okres" - dwa haki okresu wyjęte z hierarchii. */
export interface ReportPeriod {
  includes(day: LocalDate, saleDay: LocalDate): boolean;

  label(day: LocalDate): string;
}
