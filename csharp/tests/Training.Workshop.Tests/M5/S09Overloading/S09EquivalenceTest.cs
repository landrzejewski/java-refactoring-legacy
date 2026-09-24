using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S09Overloading;

/// <summary>
/// Wspólna część wszystkich wariantów: wywołanie z typem DEKLAROWANYM StudentTicket.
/// Tak wyglądał klient przed Extract Superclass - i tu wszystkie wersje są zgodne.
/// </summary>
public sealed class S09EquivalenceTest
{
    private static readonly Scene<string, string> Scene = Support.Scene.Variants<string, string>()
        .Variant("start", p => new Training.Workshop.M5.S09Overloading.Start.PriceList()
            .Price(new Training.Workshop.M5.S09Overloading.Start.StudentTicket("Amator", Money.Of(p))).ToString())
        .Variant("step1", p => new Training.Workshop.M5.S09Overloading.Step1.PriceList()
            .Price(new Training.Workshop.M5.S09Overloading.Step1.StudentTicket("Amator", Money.Of(p))).ToString())
        .Variant("step2", p => new Training.Workshop.M5.S09Overloading.Step2.PriceList()
            .Price(new Training.Workshop.M5.S09Overloading.Step2.StudentTicket("Amator", Money.Of(p))).ToString())
        .Expect("studencki 2D", "25.00", "18.75")
        .Expect("studencki IMAX", "40.00", "30.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepPricesStaticallyTypedStudentTicket(string test) => Scene.Run(test);
}
