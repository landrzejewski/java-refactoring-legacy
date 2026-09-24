namespace Training.Workshop.M8.S13LivingDocs.Start;

/// <summary>
/// Start: routing fasady Strangler Fig jako kod - źródło prawdy. Obok leży ROUTING.md pisany
/// ręcznie: raport przejęto miesiąc temu, anulowanie dodano tydzień temu, a dokument o tym nie wie.
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
