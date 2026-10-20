namespace Training.Workshop.M5.S32Mediator.Step3;

/// <summary>
/// Stabilny punkt wejścia sceny. Krok 3: ekran jest jedynym miejscem, które zna wszystkie cztery
/// komponenty; każdy komponent zna tylko interfejs ISelectionMediator (4 referencje zamiast 11).
/// Nowy komponent (np. licznik czasu rezerwacji) to zmiana w mediatorze, a nie w czterech klasach.
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

    public string PayRequested()
    {
        var paid = _basket.Seats;
        var amount = _summary.Total;
        _seatMap.MarkSold(paid);
        _basket.Clear();
        return "Zaplacono " + amount + " za " + string.Join(",", paid);
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
