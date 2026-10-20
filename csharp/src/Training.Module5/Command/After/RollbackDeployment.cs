namespace Training.Module5.Command.After;

public sealed class RollbackDeployment : IDeploymentCommand
{
    public string Execute(string releaseId) => "rolled-back:" + releaseId;
}
