namespace Training.Module6.Decorator.After;

public sealed class AuditedDeploymentRunner : IDeploymentRunner
{
    private readonly IDeploymentRunner _delegate;
    private readonly Action<string> _audit;

    public AuditedDeploymentRunner(IDeploymentRunner @delegate, Action<string> audit)
    {
        ArgumentNullException.ThrowIfNull(@delegate);
        ArgumentNullException.ThrowIfNull(audit);
        _delegate = @delegate;
        _audit = audit;
    }

    public string Run(string? releaseId)
    {
        _audit("start:" + releaseId);
        try
        {
            var result = _delegate.Run(releaseId);
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
