namespace Training.Module7.BreakResponsibilities;

public sealed record DeploymentSample
{
    public DeploymentSample(long leadTimeMinutes, bool successful)
    {
        if (leadTimeMinutes < 0)
        {
            throw new ArgumentException("leadTimeMinutes must not be negative");
        }
        LeadTimeMinutes = leadTimeMinutes;
        Successful = successful;
    }

    public long LeadTimeMinutes { get; }

    public bool Successful { get; }
}
