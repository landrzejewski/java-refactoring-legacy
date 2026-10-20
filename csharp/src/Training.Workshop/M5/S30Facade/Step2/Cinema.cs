namespace Training.Workshop.M5.S30Facade.Step2;

/// <summary>Stabilny punkt wejścia sceny (korzeń kompozycji). Krok 2: oba kanały dostają tylko fasadę.</summary>
public sealed class Cinema
{
    private readonly SeatRegistry _seats = new();
    private readonly PriceList _prices = new();
    private readonly PaymentClient _payments = new();
    private readonly TicketMailer _mailer = new();
    private readonly BookingFacade _booking;
    private readonly OnlineShop _onlineShop;
    private readonly BoxOffice _boxOffice;

    public Cinema()
    {
        _booking = new BookingFacade(_seats, _prices, _payments, _mailer);
        _onlineShop = new OnlineShop(_booking);
        _boxOffice = new BoxOffice(_booking);
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
