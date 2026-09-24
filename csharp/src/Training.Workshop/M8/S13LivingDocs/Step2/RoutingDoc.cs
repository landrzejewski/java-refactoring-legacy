using System.Text;

namespace Training.Workshop.M8.S13LivingDocs.Step2;

/// <summary>
/// Krok 2: dokument pokazuje też architekturę przejściową - właściciela i kryterium usunięcia
/// każdej trasy. Informacja operacyjna jest żywa, uzasadnienie decyzji zostaje w ADR (historia).
/// </summary>
public static class RoutingDoc
{
    public static string Render(IReadOnlyList<Routing.Route> routes)
    {
        StringBuilder md = new StringBuilder("# Routing CineLegacy\n\n")
            .Append("Plik generowany z Routing.Routes() przez RoutingDoc - nie edytuj ręcznie.\n\n")
            .Append("| Operacja | Obsługuje | Właściciel | Usunąć, gdy |\n")
            .Append("| --- | --- | --- | --- |\n");
        foreach (Routing.Route route in routes)
        {
            md.Append("| " + route.Operation + " | " + route.Target + " | " + route.Owner
                + " | " + route.RemoveWhen + " |").Append('\n');
        }
        return md.ToString();
    }

    /// <summary>
    /// Ścieżka dokumentu obok kodu, względem katalogu csharp/ - liczona z przestrzeni nazw,
    /// więc działa też po "jump" do Start.
    /// </summary>
    public static string Location()
    {
        string relative = typeof(RoutingDoc).Namespace!["Training.Workshop.".Length..].Replace('.', '/');
        return Path.Combine("src", "Training.Workshop", relative, "ROUTING.md");
    }

    /// <summary>Regeneracja dokumentu - wywołaj z katalogu csharp/ repozytorium (np. z C# Interactive w IDE: RoutingDoc.Regenerate()).</summary>
    public static void Regenerate()
    {
        File.WriteAllText(Location(), Render(Routing.Routes()));
    }
}
