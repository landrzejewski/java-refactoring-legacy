namespace Training.Workshop.M7.S10BooleanParameter.Step3;

/// <summary>
/// Krok 3: klient zmigrowany. customerHasGlasses zostaje boolem - to dana z formularza,
/// tłumaczymy ją na Glasses na granicy.
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
        var glasses = customerHasGlasses ? Glasses.Own : Glasses.Rented;
        return _tickets.BookAtBoxOffice(title, format, seats, glasses);
    }
}
