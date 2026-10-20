namespace Training.Workshop.M5.S32Mediator.Step3;

/// <summary>
/// Krok 3: kontrakt mediatora. Komponenty zgłaszają tylko zdarzenia; dane potrzebne do reakcji
/// zbiera mediator. Przycisk zamiast "zapłacono za te miejsca" mówi "użytkownik chce zapłacić".
/// </summary>
/// <remarks>
/// Mediator a Observer: obserwator rozgłasza zdarzenie nieznanym odbiorcom, każdy reaguje sam.
/// Mediator zna wszystkich uczestników i świadomie koordynuje ich reakcję (kolejność, warunki).
/// Ryzyko: mediator łatwo rośnie w "boską klasę" - trzymaj w nim tylko koordynację, nie logikę
/// komponentów (cena dalej liczy się w PriceSummary, limit miejsc w PayButton).
/// </remarks>
public interface ISelectionMediator
{
    void SeatToggled(string seat, bool selected);

    void SeatRemoved(string seat);

    void BasketCleared();

    void VoucherApplied();

    /// <summary>Zwraca komunikat dla użytkownika.</summary>
    string PayRequested();
}
