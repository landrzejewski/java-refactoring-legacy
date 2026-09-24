/**
 * Stabilny kontrakt sceny: publiczne API kina widziane przez klientów (kasa, strona www).
 * Strangler Fig działa na tej granicy - klienci nie wiedzą, kto obsługuje wywołanie.
 */
export interface CinemaApi {
  /** Zwraca identyfikator rezerwacji (B1, B2, ...) albo komunikat "ERROR: ...". */
  book(email: string, title: string, format: number, tickets: number, web: boolean): string;

  /** Raport sprzedaży ze wspólnej bazy rezerwacji. */
  report(): string;
}
