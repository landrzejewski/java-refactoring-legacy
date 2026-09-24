using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S02PullUpField;

/// <summary>Test równoważności: opis biletu (z normalizacją VIP) jest identyczny w start i każdym kroku.</summary>
public sealed class S02EquivalenceTest
{
    public sealed record Sale(string Kind, string Seat, string? StudentId);

    private static readonly Scene<Sale, string> Scene = Support.Scene.Variants<Sale, string>()
        .Variant("start", s => new Training.Workshop.M5.S02PullUpField.Start.BoxOffice().Describe(s.Kind, s.Seat, s.StudentId))
        .Variant("step1", s => new Training.Workshop.M5.S02PullUpField.Step1.BoxOffice().Describe(s.Kind, s.Seat, s.StudentId))
        .Variant("step2", s => new Training.Workshop.M5.S02PullUpField.Step2.BoxOffice().Describe(s.Kind, s.Seat, s.StudentId))
        .Variant("step3", s => new Training.Workshop.M5.S02PullUpField.Step3.BoxOffice().Describe(s.Kind, s.Seat, s.StudentId))
        .Expect("normalny - miejsce bez normalizacji", new Sale("NORMAL", "h7", null), "NORMAL h7")
        .Expect("studencki", new Sale("STUDENT", "F3", "S-123"), "STUDENT F3 (legitymacja S-123)")
        .Expect("VIP - wielkie litery", new Sale("VIP", "k12", null), "VIP K12");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepDescribesSeatsTheSameWay(string test) => Scene.Run(test);
}
