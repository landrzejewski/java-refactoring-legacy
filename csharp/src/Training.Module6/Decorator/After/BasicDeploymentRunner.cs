namespace Training.Module6.Decorator.After;

public sealed class BasicDeploymentRunner : IDeploymentRunner
{
    public string Run(string? releaseId)
    {
        if (string.IsNullOrWhiteSpace(releaseId))
        {
            throw new ArgumentException("releaseId must not be blank");
        }
        return "deployed:" + releaseId;
    }
}
