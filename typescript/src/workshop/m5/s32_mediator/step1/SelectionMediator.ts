/**
 * Krok 1: Mediator - komponent zgłasza, CO się stało, a mediator decyduje, KTO ma zareagować.
 * Na razie jedno zdarzenie: kliknięcie miejsca na mapie.
 */
export interface SelectionMediator {
  seatToggled(seat: string, selected: boolean): void;
}
