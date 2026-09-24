using Training.Workshop.M8.S10QualityGate;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M8.S10QualityGate;

/// <summary>
/// Bramka budowana krok po kroku na próbkach domeny. Ścieżki do próbek wyznacza SourceFiles
/// (próbki i ich testy nie wchodzą do kompilacji projektu - bramka kompiluje je w pamięci).
/// </summary>
public sealed class S10SolutionTest
{
    internal static readonly GateInput Dirty = Input("Dirty", "Dirty");
    internal static readonly GateInput Clean = Input("Clean", "Clean");
    internal static readonly GateInput FailingTests = Input("Clean", "Broken");

    private static readonly IReadOnlyList<string> Scan = ["TODO PriceTable.cs:15", "Console PriceTable.cs:26"];

    private static readonly IReadOnlyList<string> Compiler =
    [
        "kompilator PriceTable.cs:11 CS0649",
        "kompilator PriceTable.cs:11 CS8618",
    ];

    private static readonly IReadOnlyList<string> Coverage =
    [
        "pokrycie PriceTable.VipSurcharge bez testu",
        "pokrycie PriceTable.LookupCount bez testu",
    ];

    [Fact]
    public void StartPassesEverythingBecauseItChecksNothing()
    {
        Assert.True(new Training.Workshop.M8.S10QualityGate.Start.QualityGate().Passes(Dirty));
    }

    [Fact]
    public void Step1FindsTodoAndConsoleOutput()
    {
        Assert.Equal(Scan, new Training.Workshop.M8.S10QualityGate.Step1.QualityGate().Evaluate(Dirty));
    }

    [Fact]
    public void Step2AddsCompilerWarnings()
    {
        Assert.Equal(Concat(Compiler, Scan),
            new Training.Workshop.M8.S10QualityGate.Step2.QualityGate().Evaluate(Dirty));
    }

    [Fact]
    public void Step3AddsCoverageOfTheKeyClass()
    {
        Assert.Equal(Concat(Compiler, Scan, Coverage),
            new Training.Workshop.M8.S10QualityGate.Step3.QualityGate().Evaluate(Dirty));
    }

    [Fact]
    public void Step4AddsGreenTestsAndCatchesAFailingOne()
    {
        var gate = new Training.Workshop.M8.S10QualityGate.Step4.QualityGate();
        // testy próbki brudnej są zielone
        Assert.Equal(Concat(Compiler, Scan, Coverage), gate.Evaluate(Dirty));
        Assert.Equal(["test PriceTableTest.VipSurchargeStartsAtRowNine nie przechodzi: AssertionException"],
            gate.Evaluate(FailingTests));
        Assert.False(gate.Passes(FailingTests));
        Assert.True(gate.Passes(Clean));
    }

    [Fact]
    public void EarlierGatesDoNotSeeFailingTests()
    {
        Assert.True(new Training.Workshop.M8.S10QualityGate.Step3.QualityGate().Passes(FailingTests));
    }

    private static GateInput Input(string sources, string tests)
    {
        string sourceDir = SourceFiles.Workshop("M8", "S10QualityGate", "Sample", sources);
        string testFile = SourceFiles.Tests("M8", "S10QualityGate", "Sample", tests, "PriceTableTest.cs");
        if (!Directory.Exists(sourceDir) || !File.Exists(testFile))
        {
            throw new InvalidOperationException("brak próbki " + sourceDir + " albo " + testFile);
        }
        return new GateInput(sourceDir, testFile, "PriceTable",
            "Training.Workshop.Tests.M8.S10QualityGate.Sample." + tests + ".PriceTableTest");
    }

    private static IReadOnlyList<string> Concat(params IReadOnlyList<string>[] parts)
    {
        return parts.SelectMany(part => part).ToList();
    }
}
