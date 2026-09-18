using Training.Module8.Risk;

namespace Training.Module8.Tests.Risk;

public sealed class RolloutSnapshotTest
{
    [Fact]
    public void CalculatesErrorRateAndDefinesItForAnEmptySample()
    {
        Assert.Equal(
            0.25,
            new RolloutSnapshot(20, 5, 0, 100.0).ErrorRate);
        Assert.Equal(
            0.0,
            new RolloutSnapshot(0, 0, 0, 0.0).ErrorRate);
    }

    [Fact]
    public void AcceptsCountsAtBothBoundaries()
    {
        _ = new RolloutSnapshot(1, 0, 0, 0.0);
        _ = new RolloutSnapshot(1, 1, 1, double.MaxValue);
    }

    [Fact]
    public void RejectsNegativeOrInconsistentCounts()
    {
        Assert.Throws<ArgumentException>(
            () => new RolloutSnapshot(-1, 0, 0, 0.0));
        Assert.Throws<ArgumentException>(
            () => new RolloutSnapshot(10, -1, 0, 0.0));
        Assert.Throws<ArgumentException>(
            () => new RolloutSnapshot(10, 11, 0, 0.0));
        Assert.Throws<ArgumentException>(
            () => new RolloutSnapshot(10, 0, -1, 0.0));
        Assert.Throws<ArgumentException>(
            () => new RolloutSnapshot(10, 0, 11, 0.0));
    }

    [Fact]
    public void RejectsInvalidLatencyMeasurements()
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
                () => new RolloutSnapshot(10, 0, 0, invalid));
        }
    }

    [Fact]
    public void RequiresZeroLatencyForAnEmptySample()
    {
        // double.Epsilon w C# to najmniejsza dodatnia wartość (Double.MIN_VALUE w Javie).
        Assert.Throws<ArgumentException>(
            () => new RolloutSnapshot(0, 0, 0, double.Epsilon));
    }
}
