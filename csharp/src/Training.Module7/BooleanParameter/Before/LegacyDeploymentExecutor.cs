namespace Training.Module7.BooleanParameter.Before;

public sealed class LegacyDeploymentExecutor
{
    public string Execute(string deploymentId, bool dryRun)
    {
        Validate(deploymentId);

        if (dryRun)
        {
            return "preview:" + deploymentId;
        }
        return "deployed:" + deploymentId;
    }

    private static void Validate(string deploymentId)
    {
        if (string.IsNullOrWhiteSpace(deploymentId))
        {
            throw new ArgumentException("deploymentId must not be blank");
        }
    }
}
