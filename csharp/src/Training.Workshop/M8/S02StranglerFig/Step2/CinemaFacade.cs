namespace Training.Workshop.M8.S02StranglerFig.Step2;

/// <summary>
/// Krok 2: przejęcie pierwszej ścieżki - rezerwacje obsługuje nowy BookingModule,
/// raport nadal legacy. Oba czytają i piszą tę samą bazę, więc raport widzi nowe rezerwacje.
/// </summary>
public sealed class CinemaFacade : ICinemaApi
{
    private readonly LegacyCinema _legacy;
    private readonly BookingModule _bookings;

    public CinemaFacade(BookingLedger ledger)
    {
        _legacy = new LegacyCinema(ledger);
        _bookings = new BookingModule(ledger);
    }

    public string Book(string email, string title, int format, int tickets, bool web)
    {
        return _bookings.Book(email, title, format, tickets, web);
    }

    public string Report()
    {
        return _legacy.Report();
    }

    /// <summary>Żywa dokumentacja routingu: kto obsługuje którą operację.</summary>
    public IReadOnlyDictionary<string, string> Routes()
    {
        return new Dictionary<string, string> { ["book"] = "new", ["report"] = "legacy" };
    }
}
