using Training.Workshop.M4.S07MoveMethod;

namespace Training.Workshop.Tests.M4.S07MoveMethod;

/// <summary>
/// Dokumentuje pułapkę typu docelowego: przy przenoszeniu ktoś "sprząta" parametr int? -&gt; int,
/// bo w nowym właścicielu wszystkie numery są nie-nullowalne. Kod się kompiluje, ale wybiera inne przeciążenie.
/// </summary>
public sealed class S07OverloadTrapTest
{
    [Fact]
    public void NullableParameterRemovesTheSeatNumber()
    {
        Assert.Equal([2, 3, 4, 5], new Training.Workshop.M4.S07MoveMethod.Step2.Screening(
            "Diuna", 3, S07EquivalenceTest.Diuna.Start, 1, [1, 2, 3, 4, 5]).FreeSeatsWithout(1));
    }

    [Fact]
    public void IntParameterRemovesTheElementAtIndex()
    {
        // Remove(int index) usunął miejsce nr 2, a zostawił zarezerwowane nr 1
        Assert.Equal([1, 3, 4, 5], NaiveFreeSeatsWithout([1, 2, 3, 4, 5], 1));
        Assert.Throws<ArgumentOutOfRangeException>(() => NaiveFreeSeatsWithout([7, 8, 9], 8));
    }

    /// <summary>Tak wyglądałoby Screening.FreeSeatsWithout po "sprzątaniu" typu parametru.</summary>
    private static SeatList NaiveFreeSeatsWithout(IReadOnlyList<int> freeSeats, int seat)
    {
        var free = new SeatList(freeSeats);
        free.Remove(seat);
        return free;
    }
}
