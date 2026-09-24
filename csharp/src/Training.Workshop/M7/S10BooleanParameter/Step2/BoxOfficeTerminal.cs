namespace Training.Workshop.M7.S10BooleanParameter.Step2;

/// <summary>
/// Klient 2 (jeszcze niezmigrowany): kompilator ostrzega (CS0618) o użyciu przestarzałego Book().
/// customerHasGlasses to dana z formularza, nie flaga sterująca -
/// nie każdy bool jest zapachem.
/// </summary>
public sealed class BoxOfficeTerminal
{
    private readonly TicketService _tickets;

    public BoxOfficeTerminal(TicketService tickets)
    {
        ArgumentNullException.ThrowIfNull(tickets);
        _tickets = tickets;
    }

    public string Sell(string title, string format, int seats, bool customerHasGlasses)
    {
        // Kompilator zgłasza CS0618 (przestarzałe API) - to lista klientów do migracji.
        // Projekt ma TreatWarningsAsErrors, więc do czasu migracji (krok 3) tłumimy je lokalnie i jawnie.
#pragma warning disable CS0618
        return _tickets.Book(title, format, seats, false, customerHasGlasses);
#pragma warning restore CS0618
    }
}
