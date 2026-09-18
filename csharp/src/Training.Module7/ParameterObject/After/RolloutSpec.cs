namespace Training.Module7.ParameterObject.After;

public sealed record RolloutSpec
{
    public RolloutSpec(
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
        Service = service;
        Region = region;
        Instances = instances;
        BatchSize = batchSize;
        PauseSeconds = pauseSeconds;
    }

    public string Service { get; }

    public string Region { get; }

    public int Instances { get; }

    public int BatchSize { get; }

    public int PauseSeconds { get; }
}
