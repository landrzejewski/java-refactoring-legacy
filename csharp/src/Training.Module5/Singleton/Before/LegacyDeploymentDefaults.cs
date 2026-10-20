namespace Training.Module5.Singleton.Before;

public sealed class LegacyDeploymentDefaults
{
    public TimeSpan HealthCheckTimeout { get; } = TimeSpan.FromSeconds(30);
}
