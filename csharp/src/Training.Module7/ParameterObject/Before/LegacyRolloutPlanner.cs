using static System.FormattableString;

namespace Training.Module7.ParameterObject.Before;

public sealed class LegacyRolloutPlanner
{
    private const long DeploymentSecondsPerBatch = 30;

    public long EstimateSeconds(
        string service,
        string region,
        int instances,
        int batchSize,
        int pauseSeconds)
    {
        Validate(service, region, instances, batchSize, pauseSeconds);

        var batches = ((long)instances + batchSize - 1) / batchSize;
        var deploymentSeconds = checked(batches * DeploymentSecondsPerBatch);
        var pauseTime = checked((batches - 1) * pauseSeconds);
        return checked(deploymentSeconds + pauseTime);
    }

    public string Describe(
        string service,
        string region,
        int instances,
        int batchSize,
        int pauseSeconds)
    {
        Validate(service, region, instances, batchSize, pauseSeconds);

        return Invariant(
            $"service={service};region={region};instances={instances};batchSize={batchSize};pauseSeconds={pauseSeconds}");
    }

    private static void Validate(
        string service,
        string region,
        int instances,
        int batchSize,
        int pauseSeconds)
    {
        ArgumentNullException.ThrowIfNull(service);
        if (string.IsNullOrWhiteSpace(service))
        {
            throw new ArgumentException("service must not be blank");
        }
        ArgumentNullException.ThrowIfNull(region);
        if (string.IsNullOrWhiteSpace(region))
        {
            throw new ArgumentException("region must not be blank");
        }
        if (instances <= 0)
        {
            throw new ArgumentException("instances must be greater than zero");
        }
        if (batchSize <= 0)
        {
            throw new ArgumentException("batchSize must be greater than zero");
        }
        if (pauseSeconds < 0)
        {
            throw new ArgumentException("pauseSeconds must not be negative");
        }
    }
}
