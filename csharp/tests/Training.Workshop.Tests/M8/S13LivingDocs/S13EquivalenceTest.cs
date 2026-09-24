using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M8.S13LivingDocs;

/// <summary>Test równoważności: praca nad dokumentacją nie zmienia routingu.</summary>
public sealed class S13EquivalenceTest
{
    private static readonly Scene<string, string> Scene = Support.Scene.Variants<string, string>()
        .Variant("start", op => Training.Workshop.M8.S13LivingDocs.Start.Routing.Routes()
            .First(r => r.Operation.Equals(op)).Target)
        .Variant("step1", op => Training.Workshop.M8.S13LivingDocs.Step1.Routing.Routes()
            .First(r => r.Operation.Equals(op)).Target)
        .Variant("step2", op => Training.Workshop.M8.S13LivingDocs.Step2.Routing.Routes()
            .First(r => r.Operation.Equals(op)).Target)
        .Expect("rezerwacja", "book", "new")
        .Expect("raport", "report", "new")
        .Expect("anulowanie", "cancel", "legacy");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void RoutingStaysTheSame(string test) => Scene.Run(test);
}
