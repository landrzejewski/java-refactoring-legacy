namespace Training.Module6.Command.After;

public interface IDeploymentCommand
{
    string Execute(string releaseId);
}
