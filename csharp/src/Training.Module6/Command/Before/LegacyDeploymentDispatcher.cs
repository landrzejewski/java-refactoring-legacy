namespace Training.Module6.Command.Before;

public sealed class LegacyDeploymentDispatcher
{
    public string Dispatch(DeploymentAction action, string? releaseId)
    {
        if (string.IsNullOrWhiteSpace(releaseId))
        {
            throw new ArgumentException("releaseId must not be blank");
        }

        return action switch
        {
            DeploymentAction.Pause => "paused:" + releaseId,
            DeploymentAction.Rollback => "rolled-back:" + releaseId,
            _ => throw new ArgumentOutOfRangeException(nameof(action), action, null)
        };
    }

    public enum DeploymentAction
    {
        Pause,
        Rollback
    }
}
