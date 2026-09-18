namespace Training.Module7.MiddleMan.Before;

public sealed class ReleaseDashboard
{
    private readonly ReleaseService _releaseService;

    public ReleaseDashboard(ReleaseService releaseService)
    {
        ArgumentNullException.ThrowIfNull(releaseService);
        _releaseService = releaseService;
    }

    public string Render(string deploymentId) =>
        deploymentId + " -> " + _releaseService.StatusOf(deploymentId).ToString().ToUpperInvariant();
}
