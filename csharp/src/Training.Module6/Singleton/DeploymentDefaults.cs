namespace Training.Module6.Singleton;

/// <summary>
/// Java uses a single-constant enum; the C# idiom is a sealed class with a
/// private constructor and a static, thread-safely initialized instance.
/// </summary>
public sealed class DeploymentDefaults
{
    private DeploymentDefaults()
    {
    }

    public static DeploymentDefaults Instance { get; } = new();

    public TimeSpan HealthCheckTimeout { get; } = TimeSpan.FromSeconds(30);
}
