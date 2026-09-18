using Training.Module8.Risk;

namespace Training.Module8.Tests.Risk;

public sealed class RolloutThresholdsTest
{
    [Fact]
    public void AcceptsInclusiveNumericBoundaries()
    {
        Assert.Equal(
            new RolloutThresholds(1, 0.0, 0.0),
            new RolloutThresholds(1, 0.0, 0.0));
        Assert.Equal(
            1.0,
            new RolloutThresholds(long.MaxValue, 1.0, double.MaxValue)
                .MaximumErrorRate);
    }

    [Fact]
    public void RejectsNonPositiveMinimumSampleSize()
    {
        foreach (long invalid in new long[] { long.MinValue, -1, 0 })
        {
            Assert.Throws<ArgumentException>(
                () => new RolloutThresholds(invalid, 0.05, 250.0));
        }
    }

    [Fact]
    public void RejectsInvalidErrorRates()
    {
        double[] invalidValues =
        [
            double.NegativeInfinity,
            -double.Epsilon,
            -1.0,
            Math.BitIncrement(1.0),
            double.PositiveInfinity,
            double.NaN
        ];

        foreach (double invalid in invalidValues)
        {
            Assert.Throws<ArgumentException>(
                () => new RolloutThresholds(100, invalid, 250.0));
        }
    }

    [Fact]
    public void RejectsInvalidLatencyThresholds()
    {
        double[] invalidValues =
        [
            double.NegativeInfinity,
            -double.Epsilon,
            -1.0,
            double.PositiveInfinity,
            double.NaN
        ];

        foreach (double invalid in invalidValues)
        {
            Assert.Throws<ArgumentException>(
                () => new RolloutThresholds(100, 0.05, invalid));
        }
    }
}
