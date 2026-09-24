namespace Training.Workshop.M7.S10BooleanParameter.Start;

/// <summary>Klient 1: aplikacja mobilna - zawsze online, okulary zawsze z kina.</summary>
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
        return _tickets.Book(title, format, seats, true, false);
    }
}
