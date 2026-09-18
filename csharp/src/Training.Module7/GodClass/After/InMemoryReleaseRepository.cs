namespace Training.Module7.GodClass.After;

public sealed class InMemoryReleaseRepository : IReleaseRepository
{
    private readonly OrderedDictionary<string, PublishedRelease> _releases = [];
    private readonly Action<string> _eventSink;

    public InMemoryReleaseRepository()
        : this(_ => { })
    {
    }

    public InMemoryReleaseRepository(Action<string> eventSink)
    {
        ArgumentNullException.ThrowIfNull(eventSink);
        _eventSink = eventSink;
    }

    public bool ExistsById(string releaseId) => _releases.ContainsKey(releaseId);

    public void Save(PublishedRelease release)
    {
        ArgumentNullException.ThrowIfNull(release);
        if (_releases.ContainsKey(release.ReleaseId))
        {
            throw new InvalidOperationException(
                "release already published: " + release.ReleaseId);
        }
        _releases.Add(release.ReleaseId, release);
        _eventSink("save:" + release.ReleaseId);
    }

    public IReadOnlyList<PublishedRelease> FindAll() =>
        _releases.Values.ToList().AsReadOnly();
}
