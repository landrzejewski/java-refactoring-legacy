namespace Training.Workshop.Tests.M3.S13BoundaryCheck;

/// <summary>Naprawa granicy nie zmienia ceny ani mapowania na wiersz i z powrotem.</summary>
public sealed class S13EquivalenceTest
{
    public sealed record Case(string Title, DateTime Start);

    private static readonly Support.Scene<Case, string> Scene = Support.Scene.Variants<Case, string>()
        .Variant("start", Of(Training.Workshop.M3.S13BoundaryCheck.Start.CinemaApp.Describe))
        .Variant("step1", Of(Training.Workshop.M3.S13BoundaryCheck.Step1.CinemaApp.Describe))
        .Variant("step2", Of(Training.Workshop.M3.S13BoundaryCheck.Step2.CinemaApp.Describe))
        .Expect("seans wieczorny", new Case("Amator", new DateTime(2026, 10, 2, 20, 0, 0)),
            "25.00 | ScreeningRow { Table = screenings, Title = Amator, Start = 2026-10-02 20:00:00 } | True")
        .Expect("seans poranny", new Case("Kraina Lodu", new DateTime(2026, 10, 3, 10, 30, 0)),
            "20.00 | ScreeningRow { Table = screenings, Title = Kraina Lodu, Start = 2026-10-03 10:30:00 } | True");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepPricesAndMapsTheSame(string test) => Scene.Run(test);

    private static Func<Case, string> Of(Func<string, DateTime, string> app) => c => app(c.Title, c.Start);
}
