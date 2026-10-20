using Training.Workshop.Shared;

namespace Training.Workshop.M5.S32Mediator.Step3;

/// <summary>
/// Krok 3: przycisk zna tylko mediatora - pilnuje własnej reguły (aktywny dla 1-4 miejsc)
/// i zgłasza chęć płatności. Dane o koszyku i kwocie zbiera mediator.
/// </summary>
public sealed class PayButton
{
    private const int MaxSeats = 4;

    private bool _enabled;
    private string _label = "Wybierz miejsca";
    private readonly ISelectionMediator _mediator;

    public PayButton(ISelectionMediator mediator)
    {
        ArgumentNullException.ThrowIfNull(mediator);
        _mediator = mediator;
    }

    /// <summary>Akcja użytkownika: płatność za zawartość koszyka.</summary>
    public string Press()
    {
        if (!_enabled)
        {
            return "Nieaktywny: " + _label;
        }
        return _mediator.PayRequested();
    }

    internal void Refresh(int seats, Money total)
    {
        _enabled = seats > 0 && seats <= MaxSeats;
        if (seats == 0)
        {
            _label = "Wybierz miejsca";
        }
        else if (seats > MaxSeats)
        {
            _label = "Maksymalnie " + MaxSeats + " miejsca";
        }
        else
        {
            _label = "Zaplac " + total;
        }
    }

    internal string Describe()
    {
        return "przycisk=" + _label;
    }
}
