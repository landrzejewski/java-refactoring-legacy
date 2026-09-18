namespace Training.Module6.Polymorphism.After;

public sealed record ScriptStep : DeploymentStep
{
    public ScriptStep(string? command)
    {
        if (string.IsNullOrWhiteSpace(command))
        {
            throw new ArgumentException("command must not be blank");
        }
        Command = command;
    }

    public string Command { get; }

    public override string Execute() => "executed:" + Command;
}
