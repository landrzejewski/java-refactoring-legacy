namespace Training.Workshop.M7.S11MiddleMan.Step1;

/// <summary>
/// Krok 1: pośrednik jest już czystym forwarderem - tłumaczenie "brak seansu" -&gt; 0
/// przeniesione do jedynego klienta, który na nim polega (SeatBadge).
/// </summary>
public sealed class CinemaFacade
{
    private readonly ScreeningCatalog _catalog;

    public CinemaFacade(ScreeningCatalog catalog)
    {
        ArgumentNullException.ThrowIfNull(catalog);
        _catalog = catalog;
    }

    public string Title(string id)
    {
        return _catalog.Title(id);
    }

    public string Format(string id)
    {
        return _catalog.Format(id);
    }

    public int FreeSeats(string id)
    {
        return _catalog.FreeSeats(id);
    }

    public IReadOnlyList<Screening> Screenings()
    {
        return _catalog.All();
    }
}
