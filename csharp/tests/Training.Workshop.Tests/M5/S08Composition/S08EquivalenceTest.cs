using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S08Composition;

/// <summary>
/// Test równoważności na pojedynczych kliknięciach (Add), gdzie start działa poprawnie.
/// Hurtowe UnionWith() wywołane przez typ bazowy różni się celowo - patrz S08SolutionTest.
/// </summary>
public sealed class S08EquivalenceTest
{
    private static readonly Scene<IReadOnlyList<string>, string> Scene = Support.Scene.Variants<IReadOnlyList<string>, string>()
        .Variant("start", seats =>
        {
            var selection = new Training.Workshop.M5.S08Composition.Start.SeatSelection();
            foreach (var seat in seats)
            {
                selection.Add(seat);
            }
            return selection.Count + " miejsc, " + selection.Clicks + " kliknięć";
        })
        .Variant("step1", seats =>
        {
            var selection = new Training.Workshop.M5.S08Composition.Step1.SeatSelection();
            foreach (var seat in seats)
            {
                selection.Add(seat);
            }
            return selection.Count + " miejsc, " + selection.Clicks + " kliknięć";
        })
        .Variant("step2", seats =>
        {
            var selection = new Training.Workshop.M5.S08Composition.Step2.SeatSelection();
            foreach (var seat in seats)
            {
                selection.Add(seat);
            }
            return selection.Count + " miejsc, " + selection.Clicks + " kliknięć";
        })
        .Expect("dwa miejsca", ["H7", "H8"], "2 miejsc, 2 kliknięć")
        .Expect("ponowne kliknięcie tego samego miejsca", ["H7", "H8", "H7"], "2 miejsc, 3 kliknięć")
        .Expect("nic nie wybrano", [], "0 miejsc, 0 kliknięć");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepCountsSingleClicksTheSameWay(string test) => Scene.Run(test);
}
