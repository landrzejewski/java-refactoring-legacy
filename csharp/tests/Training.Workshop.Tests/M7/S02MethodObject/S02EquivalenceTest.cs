using Training.Workshop.M7.S02MethodObject;
using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M7.S02MethodObject;

/// <summary>Test równoważności: start i każdy krok wyceniają zamówienia identycznie.</summary>
public sealed class S02EquivalenceTest
{
    private static readonly TimeOnly Evening = new(20, 0);
    private static readonly TimeOnly Morning = new(11, 0);

    private static readonly Scene<GroupOrder, Quote> Scene = Support.Scene.Variants<GroupOrder, Quote>()
        .Variant("start", new Training.Workshop.M7.S02MethodObject.Start.GroupPricing().Quote)
        .Variant("step1", new Training.Workshop.M7.S02MethodObject.Step1.GroupPricing().Quote)
        .Variant("step2", new Training.Workshop.M7.S02MethodObject.Step2.GroupPricing().Quote)
        .Variant("step3", new Training.Workshop.M7.S02MethodObject.Step3.GroupPricing().Quote)
        .Expect("IMAX wieczorem N+S+E, jedno VIP, online",
            new GroupOrder("IMAX", Evening, ["NORMAL", "STUDENT", "SENIOR"], 1, false, true),
            MakeQuote("108.00", "6.00", "114.00", 10))
        .Expect("3D rano, dwoje dzieci, okulary z kina, VIP, kasa",
            new GroupOrder("3D", Morning, ["NORMAL", "CHILD", "CHILD", "NORMAL"], 1, false, false),
            MakeQuote("104.40", "0.00", "104.40", 10))
        .Expect("grupa szkolna 2D: 8 dzieci + 2 opiekunow, online",
            new GroupOrder("2D", new TimeOnly(18, 30),
                ["CHILD", "CHILD", "CHILD", "CHILD", "CHILD", "CHILD", "CHILD", "CHILD", "NORMAL", "NORMAL"],
                0, false, true),
            MakeQuote("153.00", "20.00", "173.00", 15))
        .Expect("grupa 2D: rabat 10% z zaokragleniem (231.25 -> 208.12)",
            new GroupOrder("2D", Evening,
                ["STUDENT", "STUDENT", "STUDENT", "NORMAL", "NORMAL", "NORMAL", "NORMAL", "NORMAL", "NORMAL", "NORMAL"],
                0, false, false),
            MakeQuote("208.12", "0.00", "208.12", 20))
        .Expect("3D z wlasnymi okularami",
            new GroupOrder("3D", Evening, ["NORMAL"], 0, true, false),
            MakeQuote("32.00", "0.00", "32.00", 3))
        .Expect("puste zamowienie",
            new GroupOrder("2D", Evening, [], 0, false, true),
            MakeQuote("0.00", "0.00", "0.00", 0));

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepQuotesTheSame(string test) => Scene.Run(test);

    private static Quote MakeQuote(string tickets, string fees, string total, int points)
    {
        return new Quote(Money.Of(tickets), Money.Of(fees), Money.Of(total), points);
    }
}
