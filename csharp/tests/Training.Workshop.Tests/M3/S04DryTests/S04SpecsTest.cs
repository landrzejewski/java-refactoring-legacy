using Training.Workshop.M3.S04DryTests;

namespace Training.Workshop.Tests.M3.S04DryTests;

/// <summary>
/// Dla poprawnej taryfy każda wersja specyfikacji jest zielona. Różnica wychodzi dopiero,
/// gdy taryfa ma błąd: krok 1 (i start, z tą samą wyrocznią) go nie widzi - pułapka;
/// krok 2 go łapie. Pułapkę sprawdzamy na kopii z kroku 1, bo start jest edytowany na żywo.
/// </summary>
public sealed class S04SpecsTest
{
    private static readonly Tariff Buggy = Tariff.Standard().WithDiscount("STUDENT", 20);

    private static readonly Support.Scene<Tariff, IReadOnlyList<string>> Scene =
        Support.Scene.Variants<Tariff, IReadOnlyList<string>>()
            .Variant("start", t => new Training.Workshop.M3.S04DryTests.Start.TicketPriceSpecs()
                .Run(new TicketPrice(t)))
            .Variant("step1", t => new Training.Workshop.M3.S04DryTests.Step1.TicketPriceSpecs()
                .Run(new TicketPrice(t)))
            .Variant("step2", t => new Training.Workshop.M3.S04DryTests.Step2.TicketPriceSpecs()
                .Run(new TicketPrice(t)))
            .Expect("taryfa zgodna z regulaminem", Tariff.Standard(), []);

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryVersionAcceptsCorrectTariff(string test) => Scene.Run(test);

    [Fact]
    public void Step1IsReadableButStillBlindToTheBug()
    {
        Assert.Empty(new Training.Workshop.M3.S04DryTests.Step1.TicketPriceSpecs().Run(new TicketPrice(Buggy)));
    }

    [Fact]
    public void Step2CatchesBrokenStudentDiscount()
    {
        Assert.Equal(["student na porannym 3D: oczekiwano 19.00, jest 20.60"],
            new Training.Workshop.M3.S04DryTests.Step2.TicketPriceSpecs().Run(new TicketPrice(Buggy)));
    }
}
