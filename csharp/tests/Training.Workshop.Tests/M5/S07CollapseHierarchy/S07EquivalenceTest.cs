using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S07CollapseHierarchy;

/// <summary>Test równoważności: opis sal i reguła VIP identyczne w start i każdym kroku.</summary>
public sealed class S07EquivalenceTest
{
    private static readonly Scene<string, string> Describe = Scene.Variants<string, string>()
        .Variant("start", new Training.Workshop.M5.S07CollapseHierarchy.Start.HallCatalog().Describe)
        .Variant("step1", new Training.Workshop.M5.S07CollapseHierarchy.Step1.HallCatalog().Describe)
        .Variant("step2", new Training.Workshop.M5.S07CollapseHierarchy.Step2.HallCatalog().Describe)
        .Variant("step3", new Training.Workshop.M5.S07CollapseHierarchy.Step3.HallCatalog().Describe)
        .Expect("zwykła sala", "Sala 1", "Sala 1: 180 miejsc, VIP od rzędu 10")
        .Expect("sala IMAX - VIP w dwóch ostatnich rzędach", "Sala IMAX",
            "Sala IMAX: 308 miejsc, VIP od rzędu 13")
        .Expect("nieznana sala", "Sala 9", "brak sali: Sala 9");

    private static readonly Scene<int, bool> VipRule = Scene.Variants<int, bool>()
        .Variant("start", row => new Training.Workshop.M5.S07CollapseHierarchy.Start.HallCatalog().IsVip("Sala IMAX", row))
        .Variant("step1", row => new Training.Workshop.M5.S07CollapseHierarchy.Step1.HallCatalog().IsVip("Sala IMAX", row))
        .Variant("step2", row => new Training.Workshop.M5.S07CollapseHierarchy.Step2.HallCatalog().IsVip("Sala IMAX", row))
        .Variant("step3", row => new Training.Workshop.M5.S07CollapseHierarchy.Step3.HallCatalog().IsVip("Sala IMAX", row))
        .Expect("rząd 12 w IMAX - zwykły", 12, false)
        .Expect("rząd 13 w IMAX - VIP", 13, true);

    public static TheoryData<string> DescribeCases => Describe.Tests();

    public static TheoryData<string> VipRuleCases => VipRule.Tests();

    [Theory]
    [MemberData(nameof(DescribeCases))]
    public void EveryStepDescribesHallsTheSameWay(string test) => Describe.Run(test);

    [Theory]
    [MemberData(nameof(VipRuleCases))]
    public void EveryStepAppliesTheSameVipRule(string test) => VipRule.Run(test);
}
