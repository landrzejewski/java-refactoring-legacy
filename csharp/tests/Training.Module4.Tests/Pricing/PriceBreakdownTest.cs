using Training.Module4.Pricing;

namespace Training.Module4.Tests.Pricing;

public sealed class PriceBreakdownTest
{
    private const decimal Zero = 0.00m;

    [Fact]
    public void RejectsAmountsOutsideItsMoneyContract()
    {
        // The Java version also checks null (NullPointerException);
        // decimal is a value type, so the compiler already rules that case out.
        Assert.Multiple(
            () => Assert.Throws<ArgumentException>(() => BreakdownWithBase(-0.01m)),
            () => Assert.Throws<ArithmeticException>(() => BreakdownWithBase(1.001m)));
    }

    private static PriceBreakdown BreakdownWithBase(decimal @base)
    {
        return new PriceBreakdown(@base, Zero, Zero, Zero, Zero, Zero, Zero);
    }
}
