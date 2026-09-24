namespace Training.Workshop.M8.S02StranglerFig.Step3;

/// <summary>
/// Krok 3: przejęcie kolejnej ścieżki - raport obsługuje ReportModule. LegacyCinema
/// nie ma już żadnego ruchu, ale nadal jest w kodzie (okno wycofania).
/// </summary>
public sealed class CinemaFacade : ICinemaApi
{
    private readonly BookingModule _bookings;
    private readonly ReportModule _reports;

    public CinemaFacade(BookingLedger ledger)
    {
        _bookings = new BookingModule(ledger);
        _reports = new ReportModule(ledger);
    }

    public string Book(string email, string title, int format, int tickets, bool web)
    {
        return _bookings.Book(email, title, format, tickets, web);
    }

    public string Report()
    {
        return _reports.Report();
    }

    /// <summary>Żywa dokumentacja routingu: kto obsługuje którą operację.</summary>
    public IReadOnlyDictionary<string, string> Routes()
    {
        return new Dictionary<string, string> { ["book"] = "new", ["report"] = "new" };
    }
}
