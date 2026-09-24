namespace Training.Workshop.Tests.M5.S08Composition;

/// <summary>Pułapka dziedziczenia po kolekcji (ukrywanie zamiast nadpisania) i pułapki delegowania - dokumentacja zachowania każdego wariantu.</summary>
public sealed class S08SolutionTest
{
    private static readonly IReadOnlyList<string> RowH = ["H7", "H8"];

    [Fact]
    public void StartLosesBulkSelectionBecauseNewHidesInsteadOfOverriding()
    {
        var selection = new Training.Workshop.M5.S08Composition.Start.SeatSelection();
        // dostęp przez object, żeby test kompilował się także po naprawie start na żywo (wtedy zrobi się czerwony)
        var baseType = Assert.IsAssignableFrom<HashSet<string>>((object)selection);
        baseType.UnionWith(RowH);
        Assert.Equal(2, selection.Count);
        // pułapka: UnionWith() wywołane przez typ bazowy to metoda HashSet, a nie nasza
        Assert.Equal(0, selection.Clicks);
    }

    [Fact]
    public void StartExposesWholeSetApiThatBypassesTheCounter()
    {
        var selection = new Training.Workshop.M5.S08Composition.Start.SeatSelection();
        selection.Add("H7");
        var inheritedApi = Assert.IsAssignableFrom<ISet<string>>((object)selection);
        inheritedApi.Clear();
        var remaining = selection.Count;
        Assert.Equal(0, remaining);
        // Clear() z odziedziczonego API omija licznik
        Assert.Equal(1, selection.Clicks);
    }

    [Fact]
    public void DelegationCountsBulkSelectionOnce()
    {
        var step1 = new Training.Workshop.M5.S08Composition.Step1.SeatSelection();
        step1.UnionWith(RowH);
        Assert.Equal(2, step1.Clicks);
        var step2 = new Training.Workshop.M5.S08Composition.Step2.SeatSelection();
        step2.UnionWith(RowH);
        Assert.Equal(2, step2.Clicks);
    }

    [Fact]
    public void GeneratedGetterLeaksTheDelegate()
    {
        var selection = new Training.Workshop.M5.S08Composition.Step1.SeatSelection();
        selection.SeatSet.Add("Z1");
        Assert.Equal(1, selection.Count);
        // pułapka: zmiana przez delegata omija licznik
        Assert.Equal(0, selection.Clicks);
    }

    [Fact]
    public void SolutionIsNarrowFacadeWithDefensiveCopy()
    {
        var selection = new Training.Workshop.M5.S08Composition.Step2.SeatSelection();
        selection.UnionWith(["H8", "H7"]);
        Assert.Equal(["H8", "H7"], selection.Seats);
        Assert.Throws<NotSupportedException>(() => ((IList<string>)selection.Seats).Add("Z1"));
        // świadomie: to już nie jest ISet
        Assert.False(typeof(ISet<string>).IsAssignableFrom(typeof(Training.Workshop.M5.S08Composition.Step2.SeatSelection)));
    }
}
