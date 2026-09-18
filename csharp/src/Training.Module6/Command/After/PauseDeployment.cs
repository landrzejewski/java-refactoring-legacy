namespace Training.Module6.Command.After;

public sealed class PauseDeployment : IDeploymentCommand
{
    public string Execute(string releaseId) => "paused:" + releaseId;
}
