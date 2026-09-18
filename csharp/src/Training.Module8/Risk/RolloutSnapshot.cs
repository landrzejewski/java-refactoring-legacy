namespace Training.Module8.Risk;

public sealed record RolloutSnapshot
{
    public RolloutSnapshot(
        long sampleSize,
        long failedRequests,
        long mismatchedResponses,
        double p95LatencyMillis)
    {
        if (sampleSize < 0)
        {
            throw new ArgumentException(
                "sampleSize must not be negative");
        }
        RequireCountWithinSample(
            failedRequests, sampleSize, nameof(failedRequests));
        RequireCountWithinSample(
            mismatchedResponses, sampleSize, nameof(mismatchedResponses));
        if (!double.IsFinite(p95LatencyMillis)
            || p95LatencyMillis < 0.0)
        {
            throw new ArgumentException(
                "p95LatencyMillis must be finite and not negative");
        }
        if (sampleSize == 0 && p95LatencyMillis != 0.0)
        {
            throw new ArgumentException(
                "an empty sample must have zero p95LatencyMillis");
        }

        SampleSize = sampleSize;
        FailedRequests = failedRequests;
        MismatchedResponses = mismatchedResponses;
        P95LatencyMillis = p95LatencyMillis;
    }

    public long SampleSize { get; }

    public long FailedRequests { get; }

    public long MismatchedResponses { get; }

    public double P95LatencyMillis { get; }

    public double ErrorRate => SampleSize == 0
        ? 0.0
        : (double)FailedRequests / SampleSize;

    private static void RequireCountWithinSample(
        long count,
        long sampleSize,
        string name)
    {
        if (count < 0 || count > sampleSize)
        {
            throw new ArgumentException(
                name + " must be between 0 and sampleSize");
        }
    }
}
