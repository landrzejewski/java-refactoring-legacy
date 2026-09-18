namespace Training.Module8.BoyScout;

public sealed record DeploymentResult
{
    public DeploymentResult(
        DeploymentStatus status,
        string environment,
        string description)
    {
        if (!Enum.IsDefined(status))
        {
            throw new ArgumentOutOfRangeException(nameof(status), status, "status");
        }
        Status = status;
        Environment = Normalized(environment, nameof(environment));
        Description = Normalized(description, nameof(description));
    }

    public DeploymentStatus Status { get; }

    public string Environment { get; }

    public string Description { get; }

    private static string Normalized(string value, string name)
    {
        ArgumentNullException.ThrowIfNull(value, name);
        string normalized = value.Trim();
        if (normalized.Length == 0)
        {
            throw new ArgumentException(name + " must not be blank");
        }
        return normalized;
    }
}
