using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S11ConstructorCall;

/// <summary>Wspólna część wariantów: zwykły bilet (bez podklasy) ma tę samą etykietę w każdym kroku.</summary>
public sealed class S11EquivalenceTest
{
    private static readonly Scene<string, string> Scene = Support.Scene.Variants<string, string>()
        .Variant("start", seat => new Training.Workshop.M5.S11ConstructorCall.Start.Ticket(seat).Label())
        .Variant("step1", seat => new Training.Workshop.M5.S11ConstructorCall.Step1.Ticket(seat).Label())
        .Variant("step2", seat => new Training.Workshop.M5.S11ConstructorCall.Step2.Ticket(seat).Label())
        .Expect("zwykłe miejsce", "H7", "Miejsce H7");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepLabelsPlainTicketTheSameWay(string test) => Scene.Run(test);
}
