using Training.Workshop.M8.S01BranchByAbstraction;
using Training.Workshop.M8.S01BranchByAbstraction.Step3;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M8.S01BranchByAbstraction;

/// <summary>Test równoważności: każdy krok (i obie gałęzie przełącznika w kroku 3) potwierdza tak samo.</summary>
public sealed class S01EquivalenceTest
{
    internal static readonly Screening Diuna = new("Diuna", 3, new DateTime(2026, 3, 13, 20, 0, 0), 10);
    internal static readonly Screening KrainaLodu = new("Kraina Lodu", 2, new DateTime(2026, 3, 14, 10, 30, 0), 10);
    internal static readonly Screening Amator = new("Amator", 1, new DateTime(2026, 3, 14, 18, 0, 0), 10);
    internal static readonly Screening AmatorRano = new("Amator", 1, new DateTime(2026, 3, 15, 9, 0, 0), 10);

    internal static readonly IReadOnlyList<string> GroupSeats =
        ["A1", "B1", "C1", "D1", "E1", "F1", "G1", "H1", "I1", "J1"];

    private static readonly Scene<BookingRequest, string> Scene = Support.Scene.Variants<BookingRequest, string>()
        .Variant("start", new Training.Workshop.M8.S01BranchByAbstraction.Start.BookingService().Confirm)
        .Variant("step1", new Training.Workshop.M8.S01BranchByAbstraction.Step1.BookingService().Confirm)
        .Variant("step2", new Training.Workshop.M8.S01BranchByAbstraction.Step2.BookingService().Confirm)
        .Variant("step3 LEGACY", new Training.Workshop.M8.S01BranchByAbstraction.Step3.BookingService(
            PricingMode.Legacy).Confirm)
        .Variant("step3 MODERN", new Training.Workshop.M8.S01BranchByAbstraction.Step3.BookingService(
            PricingMode.Modern).Confirm)
        .Variant("step4", new Training.Workshop.M8.S01BranchByAbstraction.Step4.BookingService().Confirm)
        .Expect("IMAX online, student na miejscu VIP",
            new BookingRequest(Diuna, ["A5", "A10"], ["N", "S"], true, false),
            "Diuna: A5,A10 - do zaplaty 84.00")
        .Expect("3D rano w kasie, dziecko i normalny, okulary z wypożyczalni",
            new BookingRequest(KrainaLodu, ["B1", "B2"], ["C", "N"], false, false),
            "Kraina Lodu: B1,B2 - do zaplaty 47.20")
        .Expect("3D rano online, własne okulary",
            new BookingRequest(KrainaLodu, ["B1", "B2"], ["C", "N"], true, true),
            "Kraina Lodu: B1,B2 - do zaplaty 45.20")
        .Expect("grupa 10 biletów 2D online",
            new BookingRequest(Amator, GroupSeats, Enumerable.Repeat("N", 10).ToList(), true, false),
            "Amator: A1,B1,C1,D1,E1,F1,G1,H1,I1,J1 - do zaplaty 245.00")
        .Expect("senior rano na miejscu VIP",
            new BookingRequest(AmatorRano, ["C12"], ["E"], false, false),
            "Amator: C12 - do zaplaty 22.50");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepConfirmsTheSameWay(string test) => Scene.Run(test);
}
