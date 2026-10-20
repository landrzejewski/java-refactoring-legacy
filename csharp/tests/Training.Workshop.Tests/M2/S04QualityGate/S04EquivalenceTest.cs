using Training.Workshop.M2.S04QualityGate;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M2.S04QualityGate;

/// <summary>Brak fałszywych alarmów: czysty kod przechodzi każdą wersję bramki.</summary>
public sealed class S04EquivalenceTest
{
    private static readonly Scene<GateInput, string> Scene = Support.Scene.Variants<GateInput, string>()
        .Variant("start", input => Show(new Training.Workshop.M2.S04QualityGate.Start.QualityGate().Evaluate(input)))
        .Variant("step1", input => Show(new Training.Workshop.M2.S04QualityGate.Step1.QualityGate().Evaluate(input)))
        .Variant("step2", input => Show(new Training.Workshop.M2.S04QualityGate.Step2.QualityGate().Evaluate(input)))
        .Variant("step3", input => Show(new Training.Workshop.M2.S04QualityGate.Step3.QualityGate().Evaluate(input)))
        .Variant("step4", input => Show(new Training.Workshop.M2.S04QualityGate.Step4.QualityGate().Evaluate(input)))
        .Expect("czysta próbka domeny", S04SolutionTest.Clean, "[]");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void CleanCodePassesEveryVersionOfTheGate(string test) => Scene.Run(test);

    private static string Show(IReadOnlyList<string> findings) => "[" + string.Join(", ", findings) + "]";
}
