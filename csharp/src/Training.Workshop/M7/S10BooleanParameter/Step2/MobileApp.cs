namespace Training.Workshop.M7.S10BooleanParameter.Step2;

/// <summary>Klient 1 (jeszcze niezmigrowany): kompilator ostrzega (CS0618) o użyciu przestarzałego Book().</summary>
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
        // Kompilator zgłasza CS0618 (przestarzałe API) - to lista klientów do migracji.
        // Projekt ma TreatWarningsAsErrors, więc do czasu migracji (krok 3) tłumimy je lokalnie i jawnie.
#pragma warning disable CS0618
        return _tickets.Book(title, format, seats, true, false);
#pragma warning restore CS0618
    }
}
