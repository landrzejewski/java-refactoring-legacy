namespace Training.Workshop.Tests.M7.S04RemoveDuplication;

/// <summary>
/// Dokumentuje różnicę ukrytą w duplikacie: 3 x student 2D (18.75) + 7 x normalny (25.00)
/// = 231.25, rabat 23.125. Kasa (AwayFromZero) odejmuje 23.13, sklep (ToEven) 23.12.
/// Ujednolicenie trybu to zmiana kontraktu sklepu - osobny, świadomy krok 2.
/// </summary>
public sealed class S04RoundingDecisionTest
{
    internal static readonly IReadOnlyList<decimal> Edge = MakeEdge();

    [Fact]
    public void StartAndStep1KeepTheHistoricalWebRounding()
    {
        Assert.Equal("228.13",
            S04EquivalenceTest.Plain(new Training.Workshop.M7.S04RemoveDuplication.Start.WebShop().Total(Edge)));
        Assert.Equal("228.13",
            S04EquivalenceTest.Plain(new Training.Workshop.M7.S04RemoveDuplication.Step1.WebShop().Total(Edge)));
    }

    [Fact]
    public void Step2DeliberatelyAlignsWebWithBoxOffice()
    {
        Assert.Equal("228.12",
            S04EquivalenceTest.Plain(new Training.Workshop.M7.S04RemoveDuplication.Step2.WebShop().Total(Edge)));
        Assert.Equal("228.12",
            S04EquivalenceTest.Plain(new Training.Workshop.M7.S04RemoveDuplication.Step3.WebShop().Total(Edge)));
    }

    private static IReadOnlyList<decimal> MakeEdge()
    {
        var prices = S04EquivalenceTest.Prices("18.75", 3);
        prices.AddRange(S04EquivalenceTest.Prices("25.00", 7));
        return prices.AsReadOnly();
    }
}
