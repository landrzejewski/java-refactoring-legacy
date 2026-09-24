using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S10FieldHiding;

/// <summary>Wspólna część wariantów: bilet normalny opisuje się tak samo w każdym kroku.</summary>
public sealed class S10EquivalenceTest
{
    private static readonly Scene<string, string> Scene = Support.Scene.Variants<string, string>()
        .Variant("start", _ => new Training.Workshop.M5.S10FieldHiding.Start.Ticket().Label())
        .Variant("step1", _ => new Training.Workshop.M5.S10FieldHiding.Step1.Ticket().Label())
        .Variant("step2", _ => new Training.Workshop.M5.S10FieldHiding.Step2.Ticket().Label())
        .Expect("bilet normalny", "", "BILET: NORMAL");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepLabelsNormalTicketTheSameWay(string test) => Scene.Run(test);
}
