namespace Training.Workshop.M5.S22MiddleMan.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): ostatni klient zmigrowany, CinemaFacade usunięta (Safe Delete).
/// Odwrotny ruch to Hide Delegate - wrócimy do niego, gdy pośrednik zacznie coś wnosić.
/// </summary>
public sealed class DailyBoard
{
    private readonly ScreeningCatalog _catalog;

    public DailyBoard(ScreeningCatalog catalog)
    {
        ArgumentNullException.ThrowIfNull(catalog);
        _catalog = catalog;
    }

    public string Render()
    {
        return string.Join("\n", _catalog.All()
            .Select(screening => screening.Id + " " + screening.Title + " " + screening.Format));
    }
}
