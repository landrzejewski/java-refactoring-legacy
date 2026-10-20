namespace Training.Workshop.M5.S32Mediator.Step1;

/// <summary>
/// Stabilny punkt wejścia sceny: ekran wyboru miejsc. Krok 1: Extract Interface
/// ISelectionMediator + Move Method - ekran (jak "dialog" z GoF) przejmuje reakcję na kliknięcie
/// miejsca. Pozostałe komponenty nadal są połączone wprost. Mapa dostaje mediatora
/// w konstruktorze ("this" przed końcem konstrukcji - bezpieczne, bo mediator działa dopiero
/// po akcji użytkownika). C# nie pozwala użyć "this" w inicjalizatorze pola, więc mapa
/// powstaje w konstruktorze.
/// </summary>
public sealed class SeatSelectionScreen : ISelectionMediator
{
    private readonly SeatMap _seatMap;
    private readonly Basket _basket = new();
    private readonly PriceSummary _summary = new();
    private readonly PayButton _payButton = new();

    public SeatSelectionScreen()
    {
        _seatMap = new SeatMap(this);
        _basket.Connect(_seatMap, _summary, _payButton);
        _summary.Connect(_basket, _payButton);
        _payButton.Connect(_seatMap, _basket, _summary);
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
        _summary.Recalculate(_basket.Seats);
        _payButton.Refresh(_basket.Seats.Count, _summary.Total);
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
