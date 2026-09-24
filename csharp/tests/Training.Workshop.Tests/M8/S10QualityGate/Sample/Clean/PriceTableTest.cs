using Training.Workshop.M8.S10QualityGate.Sample.Clean;
using Xunit;

namespace Training.Workshop.Tests.M8.S10QualityGate.Sample.Clean;

/// <summary>Test próbki czystej: każda publiczna metoda ma przypadek.</summary>
public sealed class PriceTableTest
{
    [Fact]
    public void BasePriceDependsOnFormat()
    {
        Assert.Equal(32, new PriceTable().BasePrice("3D"));
    }

    [Fact]
    public void VipSurchargeFromRowTen()
    {
        Assert.Equal(0, new PriceTable().VipSurcharge(9));
        Assert.Equal(10, new PriceTable().VipSurcharge(10));
    }
}
