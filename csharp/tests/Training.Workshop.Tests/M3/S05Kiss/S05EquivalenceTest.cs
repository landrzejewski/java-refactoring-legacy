using Training.Workshop.M3.S05Kiss;

namespace Training.Workshop.Tests.M3.S05Kiss;

/// <summary>Prostsza wersja liczy wolne miejsca dokładnie tak samo jak "sprytna".</summary>
public sealed class S05EquivalenceTest
{
    private static readonly Support.Scene<Hall, string> Scene = Support.Scene.Variants<Hall, string>()
        .Variant("start", new Training.Workshop.M3.S05Kiss.Start.SeatCounter().Summary)
        .Variant("step1", new Training.Workshop.M3.S05Kiss.Step1.SeatCounter().Summary)
        .Variant("step2", new Training.Workshop.M3.S05Kiss.Step2.SeatCounter().Summary)
        .Expect("zajete, zablokowane i przejscie nie sa wolne",
            new Hall(["..X", "XXX", ".X.", ". B."], 3), "wolne: 6, wolne VIP: 4")
        .Expect("sala bez rzedow VIP", new Hall(["....", "...."], 10), "wolne: 8, wolne VIP: 0")
        .Expect("cala sala VIP", new Hall(["X.", ".X"], 1), "wolne: 2, wolne VIP: 2")
        .Expect("pusta lista rzedow", new Hall([], 10), "wolne: 0, wolne VIP: 0");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepCountsFreeSeatsTheSame(string test) => Scene.Run(test);
}
