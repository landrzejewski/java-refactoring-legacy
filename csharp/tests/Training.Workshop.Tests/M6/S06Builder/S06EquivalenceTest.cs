using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S06Builder;

/// <summary>Repertuar dnia zbudowany przez każdy wariant renderuje się identycznie.</summary>
public sealed class S06EquivalenceTest
{
    private static readonly Scene<DateOnly, string> Scene = Support.Scene.Variants<DateOnly, string>()
        .Variant("start", d => new Training.Workshop.M6.S06Builder.Start.WeekendPlanner().Plan(d).Render())
        .Variant("step1", d => new Training.Workshop.M6.S06Builder.Step1.WeekendPlanner().Plan(d).Render())
        .Variant("step2", d => new Training.Workshop.M6.S06Builder.Step2.WeekendPlanner().Plan(d).Render())
        .Variant("step3", d => new Training.Workshop.M6.S06Builder.Step3.WeekendPlanner().Plan(d).Render())
        .Expect("sobota", new DateOnly(2026, 10, 3), """
            2026-10-03 Saturday
            Sala 1
              18:00 Diuna
              21:00 Diuna
            Sala 2
              10:00 Kraina Lodu
              17:30 Amator
            Sala 3 VIP
              20:00 Amator
            Seansow: 5

            """)
        .Expect("piątek - pusta sala VIP", new DateOnly(2026, 10, 2), """
            2026-10-02 Friday
            Sala 1
              18:00 Diuna
              21:00 Diuna
            Sala 2
              17:30 Amator
            Sala 3 VIP
              (brak seansow)
            Seansow: 3

            """);

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepBuildsTheSameSchedule(string test) => Scene.Run(test);
}
