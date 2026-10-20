using Training.Workshop.Shared;

namespace Training.Workshop.M5.S32Mediator.Start;

/// <summary>
/// Start: przycisk płatności - aktywny dla 1-4 miejsc. Po naciśnięciu sam pyta koszyk i sumę,
/// oznacza miejsca na mapie jako sprzedane i czyści koszyk, co uruchamia kaskadę odświeżeń.
/// </summary>
public sealed class PayButton
{
    private const int MaxSeats = 4;

    private bool _enabled;
    private string _label = "Wybierz miejsca";
    private SeatMap _seatMap = null!;
    private Basket _basket = null!;
    private PriceSummary _summary = null!;

    internal void Connect(SeatMap seatMap, Basket basket, PriceSummary summary)
    {
        _seatMap = seatMap;
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
        _seatMap.MarkSold(paid);
        _basket.Clear();
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
