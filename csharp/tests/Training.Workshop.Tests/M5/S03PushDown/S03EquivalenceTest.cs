using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S03PushDown;

/// <summary>Test równoważności: sprzedaż (z dopłatą VIP i bez) daje tę samą cenę w start i każdym kroku.</summary>
public sealed class S03EquivalenceTest
{
    public sealed record Sale(string Kind, string BasePrice, bool Vip);

    private static readonly Scene<Sale, string> Scene = Support.Scene.Variants<Sale, string>()
        .Variant("start", s => new Training.Workshop.M5.S03PushDown.Start.BoxOffice()
            .Sell(s.Kind, Money.Of(s.BasePrice), s.Vip).ToString())
        .Variant("step1", s => new Training.Workshop.M5.S03PushDown.Step1.BoxOffice()
            .Sell(s.Kind, Money.Of(s.BasePrice), s.Vip).ToString())
        .Variant("step2", s => new Training.Workshop.M5.S03PushDown.Step2.BoxOffice()
            .Sell(s.Kind, Money.Of(s.BasePrice), s.Vip).ToString())
        .Variant("step3", s => new Training.Workshop.M5.S03PushDown.Step3.BoxOffice()
            .Sell(s.Kind, Money.Of(s.BasePrice), s.Vip).ToString())
        .Expect("normalny 2D", new Sale("NORMAL", "25.00", false), "25.00")
        .Expect("normalny 2D z dopłatą VIP", new Sale("NORMAL", "25.00", true), "35.00")
        .Expect("studencki 3D", new Sale("STUDENT", "32.00", false), "24.00")
        .Expect("studencki 3D - prośba o VIP ignorowana", new Sale("STUDENT", "32.00", true), "24.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepSellsForTheSamePrice(string test) => Scene.Run(test);
}
