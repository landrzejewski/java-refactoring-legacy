namespace Training.Workshop.M5.S30Facade.Start;

/// <summary>Stabilny punkt wejścia sceny (korzeń kompozycji): składa podsystemy i kanały sprzedaży.</summary>
public sealed class Cinema
{
    private readonly SeatRegistry _seats = new();
    private readonly PriceList _prices = new();
    private readonly PaymentClient _payments = new();
    private readonly TicketMailer _mailer = new();
    private readonly OnlineShop _onlineShop;
    private readonly BoxOffice _boxOffice;

    public Cinema()
    {
        _onlineShop = new OnlineShop(_seats, _prices, _payments, _mailer);
        _boxOffice = new BoxOffice(_seats, _prices, _payments, _mailer);
    }

    public OnlineShop OnlineShop => _onlineShop;

    public BoxOffice BoxOffice => _boxOffice;

    /// <summary>Obserwowalny stan wszystkich podsystemów naraz (listy w zapisie Javy: <c>[a, b]</c>).</summary>
    public string State()
    {
        return "miejsca=" + _seats.Describe()
            + " platnosci=[" + string.Join(", ", _payments.Log()) + "]"
            + " maile=[" + string.Join(", ", _mailer.Sent()) + "]";
    }
}
