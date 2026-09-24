using System.Globalization;
using Training.Workshop.M3.S06Yagni;

namespace Training.Workshop.Tests.M3.S06Yagni;

/// <summary>Silnik reguł i dwa proste warunki dają te same ceny.</summary>
public sealed class S06EquivalenceTest
{
    private static readonly Support.Scene<TicketQuote, string> Scene = Support.Scene.Variants<TicketQuote, string>()
        .Variant("start", q => Show(new Training.Workshop.M3.S06Yagni.Start.TicketPricer().Price(q)))
        .Variant("step1", q => Show(new Training.Workshop.M3.S06Yagni.Step1.TicketPricer().Price(q)))
        .Variant("step2", q => Show(new Training.Workshop.M3.S06Yagni.Step2.TicketPricer().Price(q)))
        .Variant("step3", q => Show(new Training.Workshop.M3.S06Yagni.Step3.TicketPricer().Price(q)))
        .Expect("IMAX wieczorem, zwykle miejsce", new TicketQuote("IMAX", new TimeOnly(20, 0), 5, 10), "40.00")
        .Expect("3D rano, VIP", new TicketQuote("3D", new TimeOnly(10, 0), 12, 10), "37.00")
        .Expect("2D 11:59 to jeszcze poranek, pierwszy rzad VIP",
            new TicketQuote("2D", new TimeOnly(11, 59), 10, 10), "30.00")
        .Expect("2D 12:00 to juz nie poranek", new TicketQuote("2D", new TimeOnly(12, 0), 9, 10), "25.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepPricesTheSame(string test) => Scene.Run(test);

    private static string Show(decimal amount) => amount.ToString(CultureInfo.InvariantCulture);
}
