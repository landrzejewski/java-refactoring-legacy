namespace Training.Workshop.M8.S13LivingDocs.Step2;

/// <summary>
/// Krok 2: każda trasa ma właściciela i kryterium usunięcia - element przejściowy bez nich
/// staje się nowym legacy. Routing (zachowanie) bez zmian.
/// </summary>
public static class Routing
{
    public sealed record Route(string Operation, string Target, string Owner, string RemoveWhen);

    public static IReadOnlyList<Route> Routes()
    {
        return
        [
            new Route("book", "new", "zespol Sprzedaz", "-"),
            new Route("report", "new", "zespol Raporty", "-"),
            new Route("cancel", "legacy", "zespol Sprzedaz",
                "CancelModule w trybie CANDIDATE przez 14 dni"),
        ];
    }
}
