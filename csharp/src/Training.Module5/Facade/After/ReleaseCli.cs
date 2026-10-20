namespace Training.Module5.Facade.After;

public sealed class ReleaseCli
{
    private readonly ReleasePublishingFacade _publishing;

    public ReleaseCli(ReleasePublishingFacade publishing)
    {
        ArgumentNullException.ThrowIfNull(publishing);
        _publishing = publishing;
    }

    public string Publish(string? releaseId, string environment) =>
        _publishing.Publish(releaseId, environment);
}
