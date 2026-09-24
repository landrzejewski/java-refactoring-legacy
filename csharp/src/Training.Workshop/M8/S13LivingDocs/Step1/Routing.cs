namespace Training.Workshop.M8.S13LivingDocs.Step1;

/// <summary>
/// Krok 1 (bez zmian): routing jako kod - jedyne źródło prawdy dla dokumentu.
/// </summary>
public static class Routing
{
    public sealed record Route(string Operation, string Target);

    public static IReadOnlyList<Route> Routes()
    {
        return
        [
            new Route("book", "new"),
            new Route("report", "new"),
            new Route("cancel", "legacy"),
        ];
    }
}
