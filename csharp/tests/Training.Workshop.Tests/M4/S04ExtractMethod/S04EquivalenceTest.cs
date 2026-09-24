using Training.Workshop.M4.S04ExtractMethod;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M4.S04ExtractMethod;

/// <summary>Test równoważności: Start i każdy krok dają identyczny dokument.</summary>
public sealed class S04EquivalenceTest
{
    private static readonly Scene<Order, string> Scene = Support.Scene.Variants<Order, string>()
        .Variant("start", new Training.Workshop.M4.S04ExtractMethod.Start.TicketSummary().Describe)
        .Variant("step1", new Training.Workshop.M4.S04ExtractMethod.Step1.TicketSummary().Describe)
        .Variant("step2", new Training.Workshop.M4.S04ExtractMethod.Step2.TicketSummary().Describe)
        .Variant("step3", new Training.Workshop.M4.S04ExtractMethod.Step3.TicketSummary().Describe)
        .Expect("IMAX wieczorem, jedno miejsce VIP",
            new Order("Diuna", "IMAX", new TimeOnly(20, 0), [5, 10]),
            """
            BILETY: Diuna
            Format: IMAX, start 20:00
            Miejsc: 2 (w tym VIP: 1)
            Razem: 90.00

            """)
        .Expect("3D rano bez VIP",
            new Order("Kraina Lodu", "3D", new TimeOnly(11, 0), [1, 2, 3]),
            """
            BILETY: Kraina Lodu
            Format: 3D, start 11:00
            Miejsc: 3
            Razem: 81.00

            """)
        .Expect("2D, same miejsca VIP",
            new Order("Amator", "2D", new TimeOnly(18, 30), [10, 11]),
            """
            BILETY: Amator
            Format: 2D, start 18:30
            Miejsc: 2 (w tym VIP: 2)
            Razem: 70.00

            """)
        .Expect("puste zamówienie",
            new Order("Amator", "2D", new TimeOnly(18, 30), []),
            """
            BILETY: Amator
            Format: 2D, start 18:30
            Miejsc: 0
            Razem: 0.00

            """);

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepDescribesTicketsTheSameWay(string test) => Scene.Run(test);
}
