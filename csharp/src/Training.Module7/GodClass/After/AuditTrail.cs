namespace Training.Module7.GodClass.After;

public sealed class AuditTrail
{
    private readonly List<string> _entries = [];
    private readonly Action<string> _eventSink;

    public AuditTrail()
        : this(_ => { })
    {
    }

    public AuditTrail(Action<string> eventSink)
    {
        ArgumentNullException.ThrowIfNull(eventSink);
        _eventSink = eventSink;
    }

    public void RecordPublished(PublishedRelease release)
    {
        ArgumentNullException.ThrowIfNull(release);
        _entries.Add("published:" + release.ReleaseId);
        _eventSink("audit:" + release.ReleaseId);
    }

    public IReadOnlyList<string> Entries() => _entries.ToList().AsReadOnly();
}
