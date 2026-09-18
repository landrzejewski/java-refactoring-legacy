namespace Training.Module8.Risk;

public sealed class RolloutPolicy
{
    private readonly RolloutThresholds thresholds;

    public RolloutPolicy(RolloutThresholds thresholds)
    {
        ArgumentNullException.ThrowIfNull(thresholds);
        this.thresholds = thresholds;
    }

    public RolloutDecision Decide(RolloutSnapshot snapshot)
    {
        ArgumentNullException.ThrowIfNull(snapshot);

        if (snapshot.MismatchedResponses > 0
            || snapshot.ErrorRate > thresholds.MaximumErrorRate
            || snapshot.P95LatencyMillis
                > thresholds.MaximumP95LatencyMillis)
        {
            return RolloutDecision.Rollback;
        }
        if (snapshot.SampleSize < thresholds.MinimumSampleSize)
        {
            return RolloutDecision.Hold;
        }
        return RolloutDecision.Advance;
    }
}
