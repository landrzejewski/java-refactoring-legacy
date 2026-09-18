namespace Training.Module6.TypeCode.After;

public sealed record DeploymentRequest
{
    public DeploymentRequest(string? releaseId, DeploymentZone zone)
    {
        if (string.IsNullOrWhiteSpace(releaseId))
        {
            throw new ArgumentException("releaseId must not be blank");
        }
        ArgumentNullException.ThrowIfNull(zone);
        ReleaseId = releaseId;
        Zone = zone;
    }

    public string ReleaseId { get; }

    public DeploymentZone Zone { get; }

    public bool RequiresApproval() => Zone.RequiresApproval();
}
