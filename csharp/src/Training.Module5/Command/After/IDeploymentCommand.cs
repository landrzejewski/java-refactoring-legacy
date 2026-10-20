namespace Training.Module5.Command.After;

public interface IDeploymentCommand
{
    string Execute(string releaseId);
}
