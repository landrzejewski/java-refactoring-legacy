namespace Training.Module5.Facade.After;

public sealed class ReleaseScheduler
{
    private readonly ReleasePublishingFacade _publishing;

    public ReleaseScheduler(ReleasePublishingFacade publishing)
    {
        ArgumentNullException.ThrowIfNull(publishing);
        _publishing = publishing;
    }

    public IReadOnlyList<string> PublishNightly(IEnumerable<string?> releaseIds)
    {
        var published = new List<string>();
        foreach (var releaseId in releaseIds)
        {
            published.Add(_publishing.Publish(releaseId, "staging"));
        }
        return published;
    }
}
