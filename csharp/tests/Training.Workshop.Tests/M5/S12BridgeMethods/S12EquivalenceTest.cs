using Training.Workshop.M5.S12BridgeMethods;
using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S12BridgeMethods;

/// <summary>Test równoważności: cena i lista obsługiwanych typów identyczne w start i każdym kroku.</summary>
public sealed class S12EquivalenceTest
{
    private static readonly Scene<ITicket, string> Scene = Support.Scene.Variants<ITicket, string>()
        .Variant("start", t =>
        {
            var registry = Training.Workshop.M5.S12BridgeMethods.Start.RuleRegistry.Standard();
            return registry.Price(t) + " " + Show(registry.SupportedTypes());
        })
        .Variant("step1", t =>
        {
            var registry = Training.Workshop.M5.S12BridgeMethods.Step1.RuleRegistry.Standard();
            return registry.Price(t) + " " + Show(registry.SupportedTypes());
        })
        .Variant("step2", t =>
        {
            var registry = Training.Workshop.M5.S12BridgeMethods.Step2.RuleRegistry.Standard();
            return registry.Price(t) + " " + Show(registry.SupportedTypes());
        })
        .Expect("normalny IMAX", new StandardTicket(Money.Of("40.00")),
            "40.00 [StandardTicket, StudentTicket]")
        .Expect("studencki 2D", new StudentTicket(Money.Of("25.00"), "S-123"),
            "18.75 [StandardTicket, StudentTicket]");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepPricesAndReportsTheSameWay(string test) => Scene.Run(test);

    private static string Show(IReadOnlyList<string> types) => "[" + string.Join(", ", types) + "]";
}
