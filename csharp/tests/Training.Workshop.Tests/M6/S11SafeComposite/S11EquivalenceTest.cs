using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S11SafeComposite;

/// <summary>Opis i cena zestawów bez zmian - zmienia się tylko miejsce Add() w typach.</summary>
public sealed class S11EquivalenceTest
{
    private static readonly Scene<string, string> Scene = Support.Scene.Variants<string, string>()
        .Variant("start", c => new Training.Workshop.M6.S11SafeComposite.Start.ComboCatalog().Find(c).Describe())
        .Variant("step1", c => new Training.Workshop.M6.S11SafeComposite.Step1.ComboCatalog().Find(c).Describe())
        .Variant("step2", c => new Training.Workshop.M6.S11SafeComposite.Step2.ComboCatalog().Find(c).Describe())
        .Expect("zestaw rodzinny", "family",
            "Zestaw Rodzinny 49.00 [Popcorn XL 24.00, Napoje 25.00 [Cola 9.00, Cola 9.00, Woda 7.00]]")
        .Expect("zestaw duo", "duo", "Zestaw Duo 36.00 [Popcorn L 18.00, Cola 9.00, Cola 9.00]")
        .Expect("pojedynczy produkt", "nachos", "Nachos 14.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepDescribesCombosTheSame(string test) => Scene.Run(test);
}
