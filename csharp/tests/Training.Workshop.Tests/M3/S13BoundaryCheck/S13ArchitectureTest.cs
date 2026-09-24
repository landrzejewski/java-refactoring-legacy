using Training.Workshop.M3.S13BoundaryCheck;
using Training.Workshop.Tests.Support;
using Xunit.Abstractions;

namespace Training.Workshop.Tests.M3.S13BoundaryCheck;

/// <summary>
/// Automatyczna ochrona granicy i diagnostyka spójności na plikach źródłowych sceny.
/// Start jest edytowany na żywo, więc dla niego stosujemy "zamrożone naruszenia"
/// (jak FreezingArchRule w ArchUnit): znane naruszenia są tolerowane i można je spłacać,
/// każde NOWE naruszenie zapala czerwone światło. Kroki 1 i 2 sprawdzamy dokładnie.
/// (Ścieżki przez <see cref="SourceFiles"/> - katalog csharp/ szukany w górę od wyjścia testów.)
/// </summary>
public sealed class S13ArchitectureTest(ITestOutputHelper output)
{
    private const string Ns = "Training.Workshop.M3.S13BoundaryCheck";
    private static readonly BoundaryRule DomainIsPure = new("System.Data.", ".Adapter");

    /// <summary>Zamrożone zależności (bez nazwy pliku - przeniesienie kodu nie tworzy "nowego" naruszenia).</summary>
    private static readonly IReadOnlyList<string> FrozenStartUsings =
    [
        "System.Data.SqlTypes",
        Ns + ".Start.Adapter",
    ];

    private static string Scene(params string[] parts) => SourceFiles.Workshop(["M3", "S13BoundaryCheck", .. parts]);

    [Fact]
    public void StartDomainHasNoViolationsBeyondTheFrozenOnes()
    {
        var violations = DomainIsPure.Violations(Scene("Start", "Domain"));
        output.WriteLine("s13 start - naruszenia granicy: [" + string.Join(", ", violations) + "]");
        var usings = violations.Select(v => v[(v.IndexOf(": ", StringComparison.Ordinal) + 2)..]).ToList();
        Assert.True(usings.All(FrozenStartUsings.Contains),
            "nowe naruszenie granicy: [" + string.Join(", ", violations) + "]");
    }

    [Fact]
    public void Step1StillViolatesTheBoundaryButOnlyInTheMapper()
    {
        Assert.Equal(
            [
                "ScreeningRowMapper.cs: System.Data.SqlTypes",
                "ScreeningRowMapper.cs: " + Ns + ".Step1.Adapter",
            ],
            DomainIsPure.Violations(Scene("Step1", "Domain")));
    }

    [Fact]
    public void Step2DomainIsFreeOfTechnology()
    {
        Assert.Empty(DomainIsPure.Violations(Scene("Step2", "Domain")));
    }

    [Fact]
    public void StartServiceCohesionDoesNotGetWorse()
    {
        var result = new CohesionProbe().Analyze(Scene("Start", "Domain", "ScreeningService.cs"));
        output.WriteLine("s13 start - LCOM4 ScreeningService: " + result);
        Assert.True(result.Lcom4 <= 2, "spojnosc sie pogorszyla: " + result);
    }

    [Fact]
    public void Step1ClassesAreCohesive()
    {
        var probe = new CohesionProbe();
        Assert.Equal(new CohesionProbe.Result(1, ["IsMorning, Price"]),
            probe.Analyze(Scene("Step1", "Domain", "ScreeningService.cs")));
        Assert.Equal(new CohesionProbe.Result(1, ["FromRow, ToRow"]),
            probe.Analyze(Scene("Step1", "Domain", "ScreeningRowMapper.cs")));
    }
}
