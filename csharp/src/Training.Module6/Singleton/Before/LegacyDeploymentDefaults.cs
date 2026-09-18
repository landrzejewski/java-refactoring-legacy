namespace Training.Module6.Singleton.Before;

public sealed class LegacyDeploymentDefaults
{
    public TimeSpan HealthCheckTimeout { get; } = TimeSpan.FromSeconds(30);
}
