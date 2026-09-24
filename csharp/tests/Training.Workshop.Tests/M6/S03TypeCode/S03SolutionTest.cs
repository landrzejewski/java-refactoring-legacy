using Training.Workshop.M6.S03TypeCode.Step3;

namespace Training.Workshop.Tests.M6.S03TypeCode;

/// <summary>Granica trwałości: w bazie zostaje stabilny kod, nie wartość liczbowa enuma ani nazwa stałej.</summary>
public sealed class S03SolutionTest
{
    [Fact]
    public void PersistentCodeIsNotTheOrdinal()
    {
        Assert.NotEqual((int)Format.Imax, FormatCodes.ToCode(Format.Imax));
        Assert.Equal(3, FormatCodes.ToCode(Format.Imax));
    }

    [Fact]
    public void EveryFormatSurvivesRoundTripThroughTheMapper()
    {
        foreach (var format in Enum.GetValues<Format>())
        {
            Assert.Equal(format, FormatCodes.FromCode(FormatCodes.ToCode(format)));
        }
    }
}
