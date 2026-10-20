namespace Training.Workshop.M5.S32Mediator.Step2;

/// <summary>
/// Krok 2: wszystkie zdarzenia komponentów idą przez mediatora. Uwaga na SeatsPaid: przycisk
/// nadal sam zbiera dane (miejsca, kwotę) od kolegów i tylko zgłasza wynik - to zostało na krok 3.
/// </summary>
public interface ISelectionMediator
{
    void SeatToggled(string seat, bool selected);

    void SeatRemoved(string seat);

    void BasketCleared();

    void VoucherApplied();

    void SeatsPaid(IReadOnlyList<string> seats);
}
