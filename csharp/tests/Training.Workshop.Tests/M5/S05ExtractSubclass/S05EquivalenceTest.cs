using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S05ExtractSubclass;

/// <summary>Test równoważności: linia repertuaru (opis i cena) jest identyczna w start i każdym kroku.</summary>
public sealed class S05EquivalenceTest
{
    public sealed record Entry(string Title, string Format, string? Guest);

    private static readonly Scene<Entry, string> Scene = Support.Scene.Variants<Entry, string>()
        .Variant("start", e => new Training.Workshop.M5.S05ExtractSubclass.Start.Programme().Line(e.Title, e.Format, e.Guest))
        .Variant("step1", e => new Training.Workshop.M5.S05ExtractSubclass.Step1.Programme().Line(e.Title, e.Format, e.Guest))
        .Variant("step2", e => new Training.Workshop.M5.S05ExtractSubclass.Step2.Programme().Line(e.Title, e.Format, e.Guest))
        .Variant("step3", e => new Training.Workshop.M5.S05ExtractSubclass.Step3.Programme().Line(e.Title, e.Format, e.Guest))
        .Variant("step4", e => new Training.Workshop.M5.S05ExtractSubclass.Step4.Programme().Line(e.Title, e.Format, e.Guest))
        .Expect("zwykły seans IMAX", new Entry("Diuna", "IMAX", null), "Diuna (IMAX) | 40.00")
        .Expect("premiera 2D", new Entry("Amator", "2D", "Anna Nowak"),
            "Amator (2D) - premiera, gość: Anna Nowak | 40.00")
        .Expect("premiera 3D", new Entry("Kraina Lodu", "3D", "Jan Kowalski"),
            "Kraina Lodu (3D) - premiera, gość: Jan Kowalski | 47.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepPrintsTheSameProgramme(string test) => Scene.Run(test);
}
