namespace Training.Workshop.M7.S10BooleanParameter.Step3;

/// <summary>Krok 3: klient zmigrowany - wywołanie mówi "online, okulary z kina" bez zaglądania do sygnatury.</summary>
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
