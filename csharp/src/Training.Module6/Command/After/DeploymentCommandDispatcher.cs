using System.Collections.Frozen;

namespace Training.Module6.Command.After;

public sealed class DeploymentCommandDispatcher
{
    private readonly FrozenDictionary<DeploymentAction, IDeploymentCommand> _commands;

    public DeploymentCommandDispatcher(
        IReadOnlyDictionary<DeploymentAction, IDeploymentCommand> commands)
    {
        ArgumentNullException.ThrowIfNull(commands);
        var copy = commands.ToFrozenDictionary();
        var missing = Enum.GetValues<DeploymentAction>()
            .Where(action => !copy.ContainsKey(action))
            .ToList();
        if (missing.Count > 0)
        {
            throw new ArgumentException(
                "missing commands: [" + string.Join(", ", missing) + "]");
        }
        _commands = copy;
    }

    public string Dispatch(DeploymentAction action, string? releaseId)
    {
        if (string.IsNullOrWhiteSpace(releaseId))
        {
            throw new ArgumentException("releaseId must not be blank");
        }

        return _commands[action].Execute(releaseId);
    }
}
