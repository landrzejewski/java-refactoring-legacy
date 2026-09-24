using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M8.S13LivingDocs;

/// <summary>
/// Dokumentacja żywa: ROUTING.md musi zgadzać się z kodem. Pliki sceny wyznacza SourceFiles
/// (katalog csharp/ repozytorium).
/// </summary>
public sealed class S13SolutionTest
{
    private static readonly string Scene = SourceFiles.Workshop("M8", "S13LivingDocs");

    [Fact]
    public void StartDocumentationHasDriftedFromCode()
    {
        var code = new List<KeyValuePair<string, string>>();
        foreach (var route in Training.Workshop.M8.S13LivingDocs.Start.Routing.Routes())
        {
            code.Add(new(route.Operation, route.Target));
        }
        Assert.Equal(["report: dokument mowi legacy, kod mowi new", "cancel: brak w dokumencie"],
            Drift(Read("Start"), code));
    }

    [Fact]
    public void Step1DocumentIsGeneratedFromCode()
    {
        // przy rozjeździe: ROUTING.md nieaktualny - wywołaj RoutingDoc.Regenerate() z katalogu csharp/
        Assert.Equal(Training.Workshop.M8.S13LivingDocs.Step1.RoutingDoc.Render(
            Training.Workshop.M8.S13LivingDocs.Step1.Routing.Routes()), Read("Step1"));
    }

    [Fact]
    public void Step2DocumentIsGeneratedFromCode()
    {
        // przy rozjeździe: ROUTING.md nieaktualny - wywołaj RoutingDoc.Regenerate() z katalogu csharp/
        Assert.Equal(Training.Workshop.M8.S13LivingDocs.Step2.RoutingDoc.Render(
            Training.Workshop.M8.S13LivingDocs.Step2.Routing.Routes()), Read("Step2"));
    }

    [Fact]
    public void Step2EveryTransitionalRouteHasOwnerAndRemovalCriterion()
    {
        foreach (var route in Training.Workshop.M8.S13LivingDocs.Step2.Routing.Routes())
        {
            Assert.False(string.IsNullOrWhiteSpace(route.Owner), route.Operation);
            if (route.Target.Equals("legacy"))
            {
                Assert.False(route.RemoveWhen.Equals("-"), "trasa do legacy bez kryterium usunięcia: " + route);
            }
        }
    }

    [Fact]
    public void DocumentLocationFollowsThePackage()
    {
        Assert.Equal(Path.Combine("src", "Training.Workshop", "M8", "S13LivingDocs", "Step2", "ROUTING.md"),
            Training.Workshop.M8.S13LivingDocs.Step2.RoutingDoc.Location());
    }

    private static string Read(string variant)
    {
        string doc = Path.Combine(Scene, variant, "ROUTING.md");
        Assert.True(File.Exists(doc), "brak pliku " + Path.GetFullPath(doc));
        return File.ReadAllText(doc);
    }

    /// <summary>Porównuje dwie pierwsze kolumny tabeli z dokumentu z routingiem z kodu.</summary>
    private static IReadOnlyList<string> Drift(string markdown, IReadOnlyList<KeyValuePair<string, string>> code)
    {
        var documented = new Dictionary<string, string>();
        foreach (string line in markdown.Split('\n'))
        {
            string[] cells = line.Split('|');
            if (line.StartsWith('|') && cells.Length > 2 && !string.IsNullOrWhiteSpace(cells[1])
                && !cells[1].Trim().Equals("Operacja") && !cells[1].Trim().StartsWith("---", StringComparison.Ordinal))
            {
                documented[cells[1].Trim()] = cells[2].Trim();
            }
        }
        var problems = new List<string>();
        foreach (var (operation, target) in code)
        {
            if (!documented.TryGetValue(operation, out string? inDoc))
            {
                problems.Add(operation + ": brak w dokumencie");
            }
            else if (!inDoc.Equals(target))
            {
                problems.Add(operation + ": dokument mowi " + inDoc + ", kod mowi " + target);
            }
        }
        return problems;
    }
}
