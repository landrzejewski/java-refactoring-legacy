using System.Globalization;
using Training.Workshop.M4.S02ExtractVariable;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M4.S02ExtractVariable;

/// <summary>Test równoważności: Start i każdy krok liczą tę samą cenę biletu.</summary>
public sealed class S02EquivalenceTest
{
    private static readonly TicketRequest FreeSeating = new(2, "E", new TimeOnly(18, 0), null, true);

    private static readonly Scene<TicketRequest, string> Scene = Support.Scene.Variants<TicketRequest, string>()
        .Variant("start", r => Plain(new Training.Workshop.M4.S02ExtractVariable.Start.TicketPrice().Price(r)))
        .Variant("step1", r => Plain(new Training.Workshop.M4.S02ExtractVariable.Step1.TicketPrice().Price(r)))
        .Variant("step2", r => Plain(new Training.Workshop.M4.S02ExtractVariable.Step2.TicketPrice().Price(r)))
        .Variant("step3", r => Plain(new Training.Workshop.M4.S02ExtractVariable.Step3.TicketPrice().Price(r)))
        .Expect("IMAX normalny wieczorem",
            new TicketRequest(3, "N", new TimeOnly(20, 0), 5, false), "40.00")
        .Expect("3D student rano, VIP, bez okularów",
            new TicketRequest(2, "S", new TimeOnly(10, 0), 12, false), "32.00")
        .Expect("3D senior, wolna widownia (Row = null), własne okulary", FreeSeating, "22.40")
        .Expect("2D dziecko 11:59, rząd 10 to już VIP",
            new TicketRequest(1, "C", new TimeOnly(11, 59), 10, false), "20.00")
        .Expect("3D normalny 12:00 to już nie poranek, rząd 9",
            new TicketRequest(2, "N", new TimeOnly(12, 0), 9, false), "35.00")
        .Expect("IMAX dziecko rano, wolna widownia",
            new TicketRequest(3, "C", new TimeOnly(9, 0), null, false), "19.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepPricesTicketsTheSameWay(string test) => Scene.Run(test);

    /// <summary>
    /// Dokumentuje pułapkę: wydzielenie samego porównania bez osłony null zmienia zachowanie
    /// (<c>Row.Value</c> na pustym <c>int?</c> rzuca wyjątek, zanim zadziała osłona).
    /// </summary>
    [Fact]
    public void ExtractingTheComparisonWithoutTheNullGuardThrows()
    {
        Assert.Throws<InvalidOperationException>(() =>
        {
            bool vipSeat = FreeSeating.Row!.Value >= 10;
            bool hasSeat = FreeSeating.Row != null;
            if (hasSeat && vipSeat)
            {
                Assert.Fail("nie powinno tu dojść");
            }
        });
    }

    private static string Plain(decimal price) => price.ToString("0.00", CultureInfo.InvariantCulture);
}
