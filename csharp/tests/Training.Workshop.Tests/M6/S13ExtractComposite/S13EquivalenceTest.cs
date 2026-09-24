using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S13ExtractComposite;

/// <summary>Czas i opis programów - w tym zagnieżdżenie i pusty kontener.</summary>
public sealed class S13EquivalenceTest
{
    private static readonly Scene<string, string> Scene = Support.Scene.Variants<string, string>()
        .Variant("start", c => new Training.Workshop.M6.S13ExtractComposite.Start.ProgramCatalog().Find(c).Describe())
        .Variant("step1", c => new Training.Workshop.M6.S13ExtractComposite.Step1.ProgramCatalog().Find(c).Describe())
        .Variant("step2", c => new Training.Workshop.M6.S13ExtractComposite.Step2.ProgramCatalog().Find(c).Describe())
        .Expect("maraton z przerwą", "marathon",
            "Maraton Diuna (336 min) [Diuna (155 min), Diuna: Czesc druga (166 min)]")
        .Expect("blok bez przerw", "shorts",
            "Blok Krotkie metraze (36 min) [Kot (12 min), Pies (9 min), Ryba (15 min)]")
        .Expect("zagnieżdżenie", "night",
            "Maraton Noc kina (171 min) [Blok Krotkie metraze (36 min) "
                + "[Kot (12 min), Pies (9 min), Ryba (15 min)], Amator (120 min)]")
        .Expect("pusty maraton", "empty", "Maraton Pusty (0 min) []");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepDescribesProgramsTheSame(string test) => Scene.Run(test);
}
