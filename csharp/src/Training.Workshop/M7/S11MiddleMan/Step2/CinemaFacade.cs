namespace Training.Workshop.M7.S11MiddleMan.Step2;

/// <summary>
/// Krok 2: pośrednik (czysty forwarder od kroku 1) został już tylko jednemu klientowi - DailyBoard.
/// Usuniemy go, gdy zmigrujemy ostatniego klienta.
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
