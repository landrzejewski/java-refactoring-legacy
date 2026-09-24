namespace Training.Workshop.M7.S11MiddleMan.Start;

/// <summary>
/// Start: pośrednik - prawie każda metoda deleguje 1:1 do ScreeningCatalog.
/// Prawie: FreeSeats() po cichu tłumaczy "brak seansu" na 0. Zanim usuniesz pośrednika,
/// sprawdź, co naprawdę robi (autoryzacja, logi, transakcje, translacja błędów).
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
        try
        {
            return _catalog.FreeSeats(id);
        }
        catch (KeyNotFoundException)
        {
            return 0;
        }
    }

    public IReadOnlyList<Screening> Screenings()
    {
        return _catalog.All();
    }
}
