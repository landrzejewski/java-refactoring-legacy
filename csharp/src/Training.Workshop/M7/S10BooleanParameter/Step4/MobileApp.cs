namespace Training.Workshop.M7.S10BooleanParameter.Step4;

/// <summary>Klient zmigrowany w kroku 3 - wywołanie mówi "online, okulary z kina" bez zaglądania do sygnatury.</summary>
public sealed class MobileApp
{
    private readonly TicketService _tickets;

    public MobileApp(TicketService tickets)
    {
        ArgumentNullException.ThrowIfNull(tickets);
        _tickets = tickets;
    }

    public string Buy(string title, string format, int seats)
    {
        return _tickets.BookOnline(title, format, seats, Glasses.Rented);
    }
}
