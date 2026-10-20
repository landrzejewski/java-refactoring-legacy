/**
 * Krok 2: wszystkie zdarzenia komponentów idą przez mediatora. Uwaga na seatsPaid: przycisk
 * nadal sam zbiera dane (miejsca, kwotę) od kolegów i tylko zgłasza wynik - to zostało na krok 3.
 */
export interface SelectionMediator {
  seatToggled(seat: string, selected: boolean): void;

  seatRemoved(seat: string): void;

  basketCleared(): void;

  voucherApplied(): void;

  seatsPaid(seats: readonly string[]): void;
}
