namespace Training.Module7.MiddleMan.Before;

public sealed class DeploymentRegistry
{
    private readonly Dictionary<string, DeploymentStatus> _statuses = [];

    public void Update(string deploymentId, DeploymentStatus status)
    {
        ValidateDeploymentId(deploymentId);
        // C# enums cannot be null; reject undefined values instead.
        if (!Enum.IsDefined(status))
        {
            throw new ArgumentOutOfRangeException(nameof(status), status, "status must be a defined DeploymentStatus");
        }
        _statuses[deploymentId] = status;
    }

    public DeploymentStatus StatusOf(string deploymentId)
    {
        ValidateDeploymentId(deploymentId);
        return _statuses.GetValueOrDefault(deploymentId, DeploymentStatus.Unknown);
    }

    private static void ValidateDeploymentId(string deploymentId)
    {
        if (string.IsNullOrWhiteSpace(deploymentId))
        {
            throw new ArgumentException("deploymentId must not be blank");
        }
    }
}
