using Training.Workshop.M4.S03MagicNumbers;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M4.S03MagicNumbers;

/// <summary>Test równoważności: nazwanie liczb nie zmienia ani grosza w podsumowaniu.</summary>
public sealed class S03EquivalenceTest
{
    /// <summary>Pułapka "stałej" kolekcji: readonly chroni referencję, nie zawartość.</summary>
    private readonly List<string> _discountedTypes = ["S", "E", "C"];
    private readonly IList<string> _discountedTypesImmutable = Array.AsReadOnly(["S", "E", "C"]);

    private static readonly Scene<Order, string> Scene = Support.Scene.Variants<Order, string>()
        .Variant("start", new Training.Workshop.M4.S03MagicNumbers.Start.OrderPricer().Summary)
        .Variant("step1", new Training.Workshop.M4.S03MagicNumbers.Step1.OrderPricer().Summary)
        .Variant("step2", new Training.Workshop.M4.S03MagicNumbers.Step2.OrderPricer().Summary)
        .Variant("step3", new Training.Workshop.M4.S03MagicNumbers.Step3.OrderPricer().Summary)
        .Expect("2D wieczorem online: normalny + student na VIP",
            new Order(1, new TimeOnly(18, 0), true, [new Ticket("N", 5), new Ticket("S", 10)]),
            "Bilety: 53.75, oplata: 4.00, razem: 57.75, punkty: 5")
        .Expect("3D rano w kasie: senior + dziecko na VIP",
            new Order(2, new TimeOnly(10, 30), false, [new Ticket("E", 3), new Ticket("C", 11)]),
            "Bilety: 41.60, oplata: 0.00, razem: 41.60, punkty: 4")
        .Expect("IMAX online, grupa 10 biletów",
            new Order(3, new TimeOnly(20, 0), true, Enumerable.Repeat(new Ticket("N", 1), 10).ToList()),
            "Bilety: 360.00, oplata: 20.00, razem: 380.00, punkty: 36")
        .Expect("2D 12:00 w kasie, 9 biletów VIP - jeszcze nie grupa",
            new Order(1, new TimeOnly(12, 0), false, Enumerable.Repeat(new Ticket("N", 10), 9).ToList()),
            "Bilety: 315.00, oplata: 0.00, razem: 315.00, punkty: 31");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepSummarizesOrdersTheSameWay(string test) => Scene.Run(test);

    [Fact]
    public void ReadonlyDoesNotMakeACollectionConstant()
    {
        _discountedTypes.Add("N");
        // ktoś właśnie dał zniżkę biletom normalnym
        Assert.Equal(["S", "E", "C", "N"], _discountedTypes);
        Assert.Throws<NotSupportedException>(() => _discountedTypesImmutable.Add("N"));
    }
}
