using Training.Workshop.M8.S08CompilerGate;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M8.S08CompilerGate;

/// <summary>
/// Bramka kompilatora na kodzie sceny (nullable, wszystkie fale ostrzeżeń, ostrzeżenia jako błędy):
/// każdy krok usuwa jedną klasę ostrzeżeń. Ścieżki do źródeł sceny wyznacza SourceFiles.
/// </summary>
public sealed class S08SolutionTest
{
    private static readonly string Scene = SourceFiles.Workshop("M8", "S08CompilerGate");

    [Fact]
    public void StartFailsTheGateWithFourKindsOfWarnings()
    {
        CompilerGate.Result result = CompilerGate.Check(Variant("Start"));
        Assert.False(result.Passed);
        Assert.Equal(["CS0162", "CS0618", "CS8600", "CS8602"], result.Categories());
    }

    [Fact]
    public void Step1RemovesRawTypesAndUncheckedOperations()
    {
        CompilerGate.Result result = CompilerGate.Check(Variant("Step1"));
        Assert.False(result.Passed);
        Assert.Equal(["CS0162", "CS0618"], result.Categories());
    }

    [Fact]
    public void Step2StopsUsingDeprecatedApi()
    {
        CompilerGate.Result result = CompilerGate.Check(Variant("Step2"));
        Assert.False(result.Passed);
        Assert.Equal(["CS0162"], result.Categories());
    }

    [Fact]
    public void Step3PassesTheGate()
    {
        CompilerGate.Result result = CompilerGate.Check(Variant("Step3"));
        Assert.Empty(result.Warnings);
        Assert.True(result.Passed);
    }

    [Fact]
    public void WarningsPointToFileAndLine()
    {
        IReadOnlyList<string> warnings = CompilerGate.Check(Variant("Step2")).Warnings
            .Select(warning => warning.ToString()).ToList();
        Assert.Equal(["[CS0162] OccupancyReport.cs:17"], warnings);
    }

    private static string Variant(string name)
    {
        string dir = Path.Combine(Scene, name);
        Assert.True(Directory.Exists(dir), "brak katalogu sceny " + Path.GetFullPath(dir));
        return dir;
    }
}
