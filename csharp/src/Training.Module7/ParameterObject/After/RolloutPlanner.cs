using static System.FormattableString;

namespace Training.Module7.ParameterObject.After;

public sealed class RolloutPlanner
{
    private const long DeploymentSecondsPerBatch = 30;

    public long EstimateSeconds(RolloutSpec spec)
    {
        ArgumentNullException.ThrowIfNull(spec);

        // Ceiling division in 64-bit arithmetic, so int.MaxValue instances cannot overflow.
        var batches = ((long)spec.Instances + spec.BatchSize - 1) / spec.BatchSize;
        var deploymentSeconds = checked(batches * DeploymentSecondsPerBatch);
        var pauseTime = checked((batches - 1) * spec.PauseSeconds);
        return checked(deploymentSeconds + pauseTime);
    }

    public string Describe(RolloutSpec spec)
    {
        ArgumentNullException.ThrowIfNull(spec);

        return Invariant(
            $"service={spec.Service};region={spec.Region};instances={spec.Instances};batchSize={spec.BatchSize};pauseSeconds={spec.PauseSeconds}");
    }
}
