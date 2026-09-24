using System.Globalization;
using Training.Workshop.M4.S01Rename;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M4.S01Rename;

/// <summary>
/// Test równoważności: ten sam CSV z wywołania w C# i z zadania uruchamianego z konfiguracji.
/// Drugi test to ten, który "łapie" Rename: IDE przemianowuje C#, ale nie tekst konfiguracji.
/// </summary>
public sealed class S01EquivalenceTest
{
    internal static readonly IReadOnlyList<Sale> Sales =
    [
        new Sale("Diuna", 2, Amount("80.00"), true),
        new Sale("Amator", 1, Amount("25.00"), false),
        new Sale("Diuna", 1, Amount("40.00"), false),
        new Sale("Kraina Lodu", 3, Amount("96.00"), true),
    ];

#pragma warning disable CS0618 // Calc2 jest przestarzała od kroku 2 - start i step1 nie mają innej nazwy
    private static readonly Scene<IReadOnlyList<Sale>, string> CsvScene = Scene.Variants<IReadOnlyList<Sale>, string>()
        .Variant("start", s => new Training.Workshop.M4.S01Rename.Start.SalesReport().Calc2(s, false))
        .Variant("step1", s => new Training.Workshop.M4.S01Rename.Step1.SalesReport().Calc2(s, false))
#pragma warning restore CS0618
        .Variant("step2", s => new Training.Workshop.M4.S01Rename.Step2.SalesReport().RevenueCsv(s, false))
        .Variant("step3", s => new Training.Workshop.M4.S01Rename.Step3.SalesReport().RevenueCsv(s, false))
        .Expect("wszystkie sprzedaże, nagłówek t;n;d", Sales, """
            t;n;d
            Amator;1;25.00
            Diuna;3;120.00
            Kraina Lodu;3;96.00

            """)
        .Expect("brak sprzedaży - sam nagłówek", [], "t;n;d\n");

    private static readonly Scene<IReadOnlyList<Sale>, string> JobScene = Scene.Variants<IReadOnlyList<Sale>, string>()
        .Variant("start", new Training.Workshop.M4.S01Rename.Start.ReportJob().Run)
        .Variant("step1", new Training.Workshop.M4.S01Rename.Step1.ReportJob().Run)
        .Variant("step2", new Training.Workshop.M4.S01Rename.Step2.ReportJob().Run)
        .Variant("step3", new Training.Workshop.M4.S01Rename.Step3.ReportJob().Run)
        .Expect("konfiguracja: report.method=Calc2, tylko online", Sales, """
            t;n;d
            Diuna;2;80.00
            Kraina Lodu;3;96.00

            """);

    public static TheoryData<string> CsvCases => CsvScene.Tests();

    public static TheoryData<string> JobCases => JobScene.Tests();

    [Theory]
    [MemberData(nameof(CsvCases))]
    public void EveryStepPrintsTheSameCsv(string test) => CsvScene.Run(test);

    [Theory]
    [MemberData(nameof(JobCases))]
    public void EveryStepRunsTheConfiguredJob(string test) => JobScene.Run(test);

    private static decimal Amount(string value) => decimal.Parse(value, CultureInfo.InvariantCulture);
}
