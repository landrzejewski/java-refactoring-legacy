using static Training.Workshop.Tests.M7.S06ParameterObject.S06EquivalenceTest;

namespace Training.Workshop.Tests.M7.S06ParameterObject;

/// <summary>
/// Krok 3 przesuwa moment walidacji: wyjątek leci przy tworzeniu ScreeningSlot,
/// zanim wywołamy jakąkolwiek metodę. Describe() dla złej sali przestaje działać.
/// </summary>
public sealed class S06ValidationMomentTest
{
    private static readonly Clump Hall12 = new("S1", Day, 12, "2D");
    private static readonly Clump Format4Dx = new("S1", Day, 3, "4DX");

    [Fact]
    public void UntilStep2OnlyTicketPriceRejectsWrongHall()
    {
        var expected = "S1 2026-03-10 sala 12 (2D) | EXC nie ma sali 12 (S1)";
        Assert.Equal(expected, Start(Hall12));
        Assert.Equal(expected, Step1(Hall12));
        Assert.Equal(expected, Step2(Hall12));
    }

    [Fact]
    public void Step3RejectsWrongHallWhenTheSlotIsCreated()
    {
        Assert.Equal("new ScreeningSlot -> EXC nie ma sali 12 (S1)", Step3(Hall12));
    }

    [Fact]
    public void UnknownFormatMovesTheSameWay()
    {
        Assert.Equal("S1 2026-03-10 sala 3 (4DX) | EXC nieznany format 4DX (S1)", Start(Format4Dx));
        Assert.Equal("S1 2026-03-10 sala 3 (4DX) | EXC nieznany format 4DX (S1)", Step2(Format4Dx));
        Assert.Equal("new ScreeningSlot -> EXC nieznany format 4DX (S1)", Step3(Format4Dx));
    }
}
