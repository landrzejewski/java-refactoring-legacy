using Training.Workshop.M2.S04QualityGate.Sample.Dirty;
using Xunit;

namespace Training.Workshop.Tests.M2.S04QualityGate.Sample.Dirty;

/// <summary>Test próbki "brudnej": zielony, ale sprawdza tylko BasePrice (reszta bez pokrycia).</summary>
public sealed class PriceTableTest
{
    [Fact]
    public void BasePriceDependsOnFormat()
    {
        Assert.Equal(40, new PriceTable().BasePrice("IMAX"));
        Assert.Equal(25, new PriceTable().BasePrice("2D"));
    }
}
