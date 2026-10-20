package pl.training.workshop.m5.s32_mediator.step3;

/**
 * Krok 3: kontrakt mediatora. Komponenty zgłaszają tylko zdarzenia; dane potrzebne do reakcji
 * zbiera mediator. Przycisk zamiast "zapłacono za te miejsca" mówi "użytkownik chce zapłacić".
 *
 * <p>Mediator a Observer: obserwator rozgłasza zdarzenie nieznanym odbiorcom, każdy reaguje sam.
 * Mediator zna wszystkich uczestników i świadomie koordynuje ich reakcję (kolejność, warunki).
 * Ryzyko: mediator łatwo rośnie w "boską klasę" - trzymaj w nim tylko koordynację, nie logikę
 * komponentów (cena dalej liczy się w PriceSummary, limit miejsc w PayButton).
 */
public interface SelectionMediator {
    void seatToggled(String seat, boolean selected);

    void seatRemoved(String seat);

    void basketCleared();

    void voucherApplied();

    /** Zwraca komunikat dla użytkownika. */
    String payRequested();
}
