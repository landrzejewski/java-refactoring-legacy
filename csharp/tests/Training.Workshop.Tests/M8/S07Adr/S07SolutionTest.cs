using Training.Workshop.M8.S07Adr;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M8.S07Adr;

/// <summary>
/// Wykonywalny model decyzji: start narusza ADR-0007, kolejne kroki doprowadzają do zgodności.
/// Ścieżki do źródeł sceny wyznacza SourceFiles (katalog csharp/ repozytorium).
/// </summary>
public sealed class S07SolutionTest
{
    private static readonly string Scene = SourceFiles.Workshop("M8", "S07Adr");

    [Fact]
    public void StartViolatesBothRules()
    {
        IReadOnlyList<string> violations = ArchitectureRules.Violations(Variant("Start"));
        Assert.Contains("ADR-0007/R1 TicketPricing.cs:1", violations);
        Assert.Equal(["ADR-0007/R1", "ADR-0007/R2"], RuleIds(violations));
    }

    [Fact]
    public void Step1RemovesDependencyOnNotification()
    {
        Assert.Equal(["ADR-0007/R2"], RuleIds(ArchitectureRules.Violations(Variant("Step1"))));
    }

    [Fact]
    public void Step2CompliesWithTheDecision()
    {
        Assert.Empty(ArchitectureRules.Violations(Variant("Step2")));
    }

    [Fact]
    public void AdrDocumentsEveryExecutableRule()
    {
        string adr = File.ReadAllText(Path.Combine(Scene, "ADR-0007-cennik-jako-czysty-modul.md"));
        Assert.Contains("**Status:** Zaakceptowana", adr);
        foreach (ArchitectureRules.Rule rule in ArchitectureRules.Rule.Values)
        {
            string shortId = rule.Id.Substring(rule.Id.IndexOf('/') + 1);
            Assert.True(adr.Contains("**" + shortId + ".**"), "ADR nie opisuje reguły " + rule.Id);
        }
    }

    private static string Variant(string name)
    {
        string dir = Path.Combine(Scene, name);
        Assert.True(Directory.Exists(dir), "brak katalogu sceny " + Path.GetFullPath(dir));
        return dir;
    }

    private static IReadOnlyList<string> RuleIds(IReadOnlyList<string> violations)
    {
        return violations.Select(v => v.Substring(0, v.IndexOf(' '))).Distinct().Order(StringComparer.Ordinal).ToList();
    }
}
