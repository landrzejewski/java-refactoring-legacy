using Training.Workshop.M8.S03ParallelRun;
using Training.Workshop.M8.S03ParallelRun.Step4;
using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M8.S03ParallelRun;

/// <summary>Test równoważności: klient dostaje tę samą cenę w każdym kroku i w każdym trybie migracji.</summary>
public sealed class S03EquivalenceTest
{
    internal static readonly TicketQuery ImaxNormal = new("IMAX", "NORMAL", new TimeOnly(20, 0), 5);
    internal static readonly TicketQuery ImaxStudentVip = new("IMAX", "STUDENT", new TimeOnly(20, 0), 10);
    internal static readonly TicketQuery Morning3DChild = new("3D", "CHILD", new TimeOnly(10, 30), 3);
    internal static readonly TicketQuery Morning3DNormalVip = new("3D", "NORMAL", new TimeOnly(10, 30), 12);
    internal static readonly TicketQuery Evening2DSenior = new("2D", "SENIOR", new TimeOnly(18, 0), 1);
    internal static readonly TicketQuery Morning2DStudent = new("2D", "STUDENT", new TimeOnly(9, 0), 2);
    internal static readonly TicketQuery Unknown4DX = new("4DX", "NORMAL", new TimeOnly(20, 0), 1);

    private static readonly Scene<TicketQuery, Money> Scene = Support.Scene.Variants<TicketQuery, Money>()
        .Variant("start", new Training.Workshop.M8.S03ParallelRun.Start.PriceService().Price)
        .Variant("step1", new Training.Workshop.M8.S03ParallelRun.Step1.PriceService().Price)
        .Variant("step2", new Training.Workshop.M8.S03ParallelRun.Step2.PriceService().Price)
        .Variant("step3", new Training.Workshop.M8.S03ParallelRun.Step3.PriceService().Price)
        .Variant("step4 SHADOW", new Training.Workshop.M8.S03ParallelRun.Step4.PriceService().Price)
        .Variant("step4 CANDIDATE", new Training.Workshop.M8.S03ParallelRun.Step4.PriceService(
            MigrationMode.Candidate, new Training.Workshop.M8.S03ParallelRun.Step4.VerificationReport()).Price)
        .Variant("step4 LEGACY", new Training.Workshop.M8.S03ParallelRun.Step4.PriceService(
            MigrationMode.Legacy, new Training.Workshop.M8.S03ParallelRun.Step4.VerificationReport()).Price)
        .Expect("IMAX normalny wieczorem", ImaxNormal, Money.Of("40.00"))
        .Expect("IMAX student na VIP", ImaxStudentVip, Money.Of("40.00"))
        .Expect("3D dziecko rano", Morning3DChild, Money.Of("17.20"))
        .Expect("3D normalny rano na VIP", Morning3DNormalVip, Money.Of("40.00"))
        .Expect("2D senior wieczorem", Evening2DSenior, Money.Of("17.50"))
        .Expect("2D student rano", Morning2DStudent, Money.Of("13.75"));

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void CustomerPaysTheSameInEveryStep(string test) => Scene.Run(test);
}
