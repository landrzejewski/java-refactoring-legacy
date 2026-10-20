namespace Training.Module5.Command.After;

public sealed class PauseDeployment : IDeploymentCommand
{
    public string Execute(string releaseId) => "paused:" + releaseId;
}
