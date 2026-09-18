namespace Training.Module6.Decorator.Before;

public sealed class LegacyDeploymentRunner
{
    private readonly Action<string> _audit;

    public LegacyDeploymentRunner(Action<string> audit)
    {
        ArgumentNullException.ThrowIfNull(audit);
        _audit = audit;
    }

    public string Run(string? releaseId)
    {
        _audit("start:" + releaseId);
        try
        {
            if (string.IsNullOrWhiteSpace(releaseId))
            {
                throw new ArgumentException("releaseId must not be blank");
            }
            var result = "deployed:" + releaseId;
            _audit("success:" + releaseId);
            return result;
        }
        catch (Exception exception)
        {
            _audit("failure:" + releaseId + ":" + exception.GetType().Name);
            throw;
        }
    }
}
