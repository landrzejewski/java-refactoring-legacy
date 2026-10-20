namespace Training.Workshop.M5.S32Mediator.Step1;

/// <summary>
/// Krok 1: Mediator - komponent zgłasza, CO się stało, a mediator decyduje, KTO ma zareagować.
/// Na razie jedno zdarzenie: kliknięcie miejsca na mapie.
/// </summary>
public interface ISelectionMediator
{
    void SeatToggled(string seat, bool selected);
}
