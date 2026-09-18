namespace Training.Module7.BooleanParameter.After;

public sealed class DeploymentExecutor
{
    public string Preview(string deploymentId) =>
        Execute(deploymentId, ExecutionMode.Preview);

    public string Deploy(string deploymentId) =>
        Execute(deploymentId, ExecutionMode.Deploy);

    private static string Execute(string deploymentId, ExecutionMode mode)
    {
        Validate(deploymentId);
        return ResultPrefix(mode) + deploymentId;
    }

    private static void Validate(string deploymentId)
    {
        if (string.IsNullOrWhiteSpace(deploymentId))
        {
            throw new ArgumentException("deploymentId must not be blank");
        }
    }

    private static string ResultPrefix(ExecutionMode mode) => mode switch
    {
        ExecutionMode.Preview => "preview:",
        ExecutionMode.Deploy => "deployed:",
        _ => throw new ArgumentOutOfRangeException(nameof(mode), mode, null)
    };

    private enum ExecutionMode
    {
        Preview,
        Deploy
    }
}
