namespace Training.Workshop.M5.S32Mediator.Start;

/// <summary>
/// Stabilny punkt wejścia sceny: ekran wyboru miejsc. Start: tylko tworzy komponenty
/// i łączy je "każdy z każdym" - 11 referencji między 4 komponentami.
/// </summary>
public sealed class SeatSelectionScreen
{
    private readonly SeatMap _seatMap = new();
    private readonly Basket _basket = new();
    private readonly PriceSummary _summary = new();
    private readonly PayButton _payButton = new();

    public SeatSelectionScreen()
    {
        _seatMap.Connect(_basket, _summary, _payButton);
        _basket.Connect(_seatMap, _summary, _payButton);
        _summary.Connect(_basket, _payButton);
        _payButton.Connect(_seatMap, _basket, _summary);
    }

    public SeatMap SeatMap => _seatMap;

    public Basket Basket => _basket;

    public PriceSummary PriceSummary => _summary;

    public PayButton PayButton => _payButton;

    public string Render()
    {
        return _seatMap.Describe() + " " + _basket.Describe() + " " + _summary.Describe() + " " + _payButton.Describe();
    }
}
