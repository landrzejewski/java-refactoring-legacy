using Training.Workshop.Shared;

namespace Training.Workshop.M5.S32Mediator.Step2;

/// <summary>
/// Krok 2: przycisk zgłasza płatność mediatorowi (to on oznacza miejsca i czyści koszyk),
/// ale nadal sam PYTA koszyk i podsumowanie o dane - zostały mu dwie bezpośrednie referencje.
/// </summary>
public sealed class PayButton
{
    private const int MaxSeats = 4;

    private bool _enabled;
    private string _label = "Wybierz miejsca";
    private readonly ISelectionMediator _mediator;
    private Basket _basket = null!;
    private PriceSummary _summary = null!;

    public PayButton(ISelectionMediator mediator)
    {
        ArgumentNullException.ThrowIfNull(mediator);
        _mediator = mediator;
    }

    internal void Connect(Basket basket, PriceSummary summary)
    {
        _basket = basket;
        _summary = summary;
    }

    /// <summary>Akcja użytkownika: płatność za zawartość koszyka.</summary>
    public string Press()
    {
        if (!_enabled)
        {
            return "Nieaktywny: " + _label;
        }
        var paid = _basket.Seats;
        var amount = _summary.Total;
        _mediator.SeatsPaid(paid);
        return "Zaplacono " + amount + " za " + string.Join(",", paid);
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
