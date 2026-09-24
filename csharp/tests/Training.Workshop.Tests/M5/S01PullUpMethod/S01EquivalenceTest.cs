using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S01PullUpMethod;

/// <summary>Test równoważności: start i każdy krok drukują identyczne etykiety biletów.</summary>
public sealed class S01EquivalenceTest
{
    public sealed record Sale(string Kind, string Title, string BasePrice);

    private static readonly Scene<Sale, string> Scene = Support.Scene.Variants<Sale, string>()
        .Variant("start", s => new Training.Workshop.M5.S01PullUpMethod.Start.BoxOffice()
            .Label(s.Kind, s.Title, Money.Of(s.BasePrice)))
        .Variant("step1", s => new Training.Workshop.M5.S01PullUpMethod.Step1.BoxOffice()
            .Label(s.Kind, s.Title, Money.Of(s.BasePrice)))
        .Variant("step2", s => new Training.Workshop.M5.S01PullUpMethod.Step2.BoxOffice()
            .Label(s.Kind, s.Title, Money.Of(s.BasePrice)))
        .Variant("step3", s => new Training.Workshop.M5.S01PullUpMethod.Step3.BoxOffice()
            .Label(s.Kind, s.Title, Money.Of(s.BasePrice)))
        .Expect("normalny IMAX", new Sale("NORMAL", "Diuna", "40.00"), "Diuna: 40.00")
        .Expect("studencki 2D (-25%)", new Sale("STUDENT", "Amator", "25.00"), "Amator: 18.75")
        .Expect("VIP 3D (+10.00)", new Sale("VIP", "Kraina Lodu", "32.00"), "Kraina Lodu: 42.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepLabelsTicketsTheSameWay(string test) => Scene.Run(test);
}
