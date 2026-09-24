using System.Globalization;
using Training.Workshop.M3.S14ReversiblePattern;

namespace Training.Workshop.Tests.M3.S14ReversiblePattern;

/// <summary>Model procentowy rozlicza się tak samo przed wzorcem, ze wzorcem i po jego usunięciu.</summary>
public sealed class S14EquivalenceTest
{
    public sealed record Case(int Week, string Revenue);

    private static readonly Deal Dune = new("Diuna", "PERCENT");

    private static readonly Support.Scene<Case, string> Scene = Support.Scene.Variants<Case, string>()
        .Variant("start", c => Show(new Training.Workshop.M3.S14ReversiblePattern.Start.DistributorSettlement()
            .Payout(Dune, c.Week, Parse(c.Revenue))))
        .Variant("step1", c => Show(new Training.Workshop.M3.S14ReversiblePattern.Step1.DistributorSettlement()
            .Payout(Dune, c.Week, Parse(c.Revenue))))
        .Variant("step2", c => Show(new Training.Workshop.M3.S14ReversiblePattern.Step2.DistributorSettlement()
            .Payout(Dune, c.Week, Parse(c.Revenue))))
        .Variant("step3", c => Show(new Training.Workshop.M3.S14ReversiblePattern.Step3.DistributorSettlement()
            .Payout(Dune, c.Week, Parse(c.Revenue))))
        .Expect("tydzien 1: 50%", new Case(1, "2000.00"), "1000.00")
        .Expect("tydzien 2: 40%", new Case(2, "2000.00"), "800.00")
        .Expect("tydzien 3: 35% ponizej gwarancji", new Case(3, "1000.00"), "500.00")
        .Expect("tydzien 5: 35%", new Case(5, "4000.00"), "1400.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void PercentageDealsSettleTheSame(string test) => Scene.Run(test);

    private static decimal Parse(string amount) => decimal.Parse(amount, CultureInfo.InvariantCulture);

    private static string Show(decimal amount) => amount.ToString("0.00", CultureInfo.InvariantCulture);
}
