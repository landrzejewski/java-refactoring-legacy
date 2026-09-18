using System.Globalization;

namespace Training.Module8.Risk;

public sealed record RolloutThresholds
{
    public RolloutThresholds(
        long minimumSampleSize,
        double maximumErrorRate,
        double maximumP95LatencyMillis)
    {
        if (minimumSampleSize < 1)
        {
            throw new ArgumentException(
                "minimumSampleSize must be positive");
        }
        RequireFiniteInRange(
            maximumErrorRate, 0.0, 1.0, nameof(maximumErrorRate));
        RequireFiniteInRange(
            maximumP95LatencyMillis,
            0.0,
            double.MaxValue,
            nameof(maximumP95LatencyMillis));

        MinimumSampleSize = minimumSampleSize;
        MaximumErrorRate = maximumErrorRate;
        MaximumP95LatencyMillis = maximumP95LatencyMillis;
    }

    public long MinimumSampleSize { get; }

    public double MaximumErrorRate { get; }

    public double MaximumP95LatencyMillis { get; }

    private static void RequireFiniteInRange(
        double value,
        double minimum,
        double maximum,
        string name)
    {
        if (!double.IsFinite(value) || value < minimum || value > maximum)
        {
            throw new ArgumentException(string.Format(
                CultureInfo.InvariantCulture,
                "{0} must be finite and between {1} and {2}",
                name,
                minimum,
                maximum));
        }
    }
}
