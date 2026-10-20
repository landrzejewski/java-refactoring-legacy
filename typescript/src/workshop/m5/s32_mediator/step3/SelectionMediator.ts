/**
 * Krok 3: kontrakt mediatora. Komponenty zgłaszają tylko zdarzenia; dane potrzebne do reakcji
 * zbiera mediator. Przycisk zamiast "zapłacono za te miejsca" mówi "użytkownik chce zapłacić".
 *
 * Mediator a Observer: obserwator rozgłasza zdarzenie nieznanym odbiorcom, każdy reaguje sam.
 * Mediator zna wszystkich uczestników i świadomie koordynuje ich reakcję (kolejność, warunki).
 * Ryzyko: mediator łatwo rośnie w "boską klasę" - trzymaj w nim tylko koordynację, nie logikę
 * komponentów (cena dalej liczy się w PriceSummary, limit miejsc w PayButton).
 */
export interface SelectionMediator {
  seatToggled(seat: string, selected: boolean): void;

  seatRemoved(seat: string): void;

  basketCleared(): void;

  voucherApplied(): void;

  /** Zwraca komunikat dla użytkownika. */
  payRequested(): string;
}
