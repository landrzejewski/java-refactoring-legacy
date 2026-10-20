namespace Training.Workshop.M5.S32Mediator.Step2;

/// <summary>
/// Stabilny punkt wejścia sceny. Krok 2: ekran-mediator obsługuje wszystkie zdarzenia, a kolejność
/// odświeżania (suma, potem przycisk) jest w jednej metodzie Refresh() zamiast w czterech kopiach.
/// Zostało jedno połączenie wprost: przycisk czyta koszyk i sumę.
/// </summary>
public sealed class SeatSelectionScreen : ISelectionMediator
{
    private readonly SeatMap _seatMap;
    private readonly Basket _basket;
    private readonly PriceSummary _summary;
    private readonly PayButton _payButton;

    public SeatSelectionScreen()
    {
        _seatMap = new SeatMap(this);
        _basket = new Basket(this);
        _summary = new PriceSummary(this);
        _payButton = new PayButton(this);
        _payButton.Connect(_basket, _summary);
    }

    public void SeatToggled(string seat, bool selected)
    {
        if (selected)
        {
            _basket.Add(seat);
        }
        else
        {
            _basket.Drop(seat);
        }
        Refresh();
    }

    public void SeatRemoved(string seat)
    {
        _seatMap.Deselect(seat);
        Refresh();
    }

    public void BasketCleared()
    {
        _seatMap.DeselectAll();
        Refresh();
    }

    public void VoucherApplied()
    {
        Refresh();
    }

    public void SeatsPaid(IReadOnlyList<string> seats)
    {
        _seatMap.MarkSold(seats);
        _basket.Clear();
    }

    public SeatMap SeatMap => _seatMap;

    public Basket Basket => _basket;

    public PriceSummary PriceSummary => _summary;

    public PayButton PayButton => _payButton;

    /// <summary>Jedno miejsce, które zna kolejność odświeżania: najpierw suma, potem przycisk.</summary>
    private void Refresh()
    {
        _summary.Recalculate(_basket.Seats);
        _payButton.Refresh(_basket.Seats.Count, _summary.Total);
    }

    public string Render()
    {
        return _seatMap.Describe() + " " + _basket.Describe() + " " + _summary.Describe() + " " + _payButton.Describe();
    }
}
