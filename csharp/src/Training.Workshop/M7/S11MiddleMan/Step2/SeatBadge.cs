namespace Training.Workshop.M7.S11MiddleMan.Step2;

/// <summary>Krok 2: Remove Middle Man dla pierwszego klienta - plakietka rozmawia bezpośrednio z katalogiem.</summary>
public sealed class SeatBadge
{
    private readonly ScreeningCatalog _catalog;

    public SeatBadge(ScreeningCatalog catalog)
    {
        ArgumentNullException.ThrowIfNull(catalog);
        _catalog = catalog;
    }

    public string Badge(string id)
    {
        var free = FreeSeats(id);
        if (free == 0)
        {
            return id + ": WYPRZEDANE";
        }
        return _catalog.Title(id) + " (" + _catalog.Format(id) + "): " + free + " wolnych";
    }

    private int FreeSeats(string id)
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
}
