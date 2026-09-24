using Training.Workshop.M8.S06ReviewableSeries;
using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M8.S06ReviewableSeries;

/// <summary>Test równoważności dla commitów refaktoryzacyjnych (1 i 2). Commit 3 zmienia zachowanie - osobny test.</summary>
public sealed class S06EquivalenceTest
{
    internal static readonly DateTime MondayEvening = new(2026, 3, 9, 18, 0, 0);
    internal static readonly DateTime TuesdayEvening = new(2026, 3, 10, 18, 0, 0);
    internal static readonly DateTime TuesdayMorning = new(2026, 3, 10, 10, 0, 0);

    private static readonly Scene<TicketQuery, Money> Scene = Support.Scene.Variants<TicketQuery, Money>()
        .Variant("start", new Training.Workshop.M8.S06ReviewableSeries.Start.PriceList().Price)
        .Variant("step1", new Training.Workshop.M8.S06ReviewableSeries.Step1.PriceList().Price)
        .Variant("step2", new Training.Workshop.M8.S06ReviewableSeries.Step2.PriceList().Price)
        .Expect("2D normalny, poniedziałek", new TicketQuery("2D", "NORMAL", MondayEvening, 5), Money.Of("25.00"))
        .Expect("2D normalny, wtorek", new TicketQuery("2D", "NORMAL", TuesdayEvening, 5), Money.Of("25.00"))
        .Expect("IMAX student VIP", new TicketQuery("IMAX", "STUDENT", MondayEvening, 10), Money.Of("40.00"))
        .Expect("3D dziecko rano", new TicketQuery("3D", "CHILD", TuesdayMorning, 3), Money.Of("14.20"))
        .Expect("2D senior", new TicketQuery("2D", "SENIOR", TuesdayEvening, 1), Money.Of("17.50"));

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void RefactoringCommitsDoNotChangePrices(string test) => Scene.Run(test);
}
