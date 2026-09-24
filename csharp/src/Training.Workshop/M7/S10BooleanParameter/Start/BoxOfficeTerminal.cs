namespace Training.Workshop.M7.S10BooleanParameter.Start;

/// <summary>
/// Klient 2: terminal w kasie. customerHasGlasses to dana z formularza, nie flaga sterująca -
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
        return _tickets.Book(title, format, seats, false, customerHasGlasses);
    }
}
