namespace Training.Workshop.M8.S02StranglerFig.Step1;

/// <summary>
/// Krok 1: fasada 1:1 - każde wywołanie trafia do starego systemu. Zachowanie się nie zmienia,
/// ale od teraz mamy jedno miejsce, w którym można przekierować pojedynczą operację.
/// </summary>
public sealed class CinemaFacade : ICinemaApi
{
    private readonly LegacyCinema _legacy;

    public CinemaFacade(BookingLedger ledger)
    {
        _legacy = new LegacyCinema(ledger);
    }

    public string Book(string email, string title, int format, int tickets, bool web)
    {
        return _legacy.Book(email, title, format, tickets, web);
    }

    public string Report()
    {
        return _legacy.Report();
    }

    /// <summary>Żywa dokumentacja routingu: kto obsługuje którą operację.</summary>
    public IReadOnlyDictionary<string, string> Routes()
    {
        return new Dictionary<string, string> { ["book"] = "legacy", ["report"] = "legacy" };
    }
}
