namespace Training.Module7.GodClass.After;

public sealed class ReleaseNotifier
{
    private readonly List<string> _notifications = [];
    private readonly Action<string> _eventSink;

    public ReleaseNotifier()
        : this(_ => { })
    {
    }

    public ReleaseNotifier(Action<string> eventSink)
    {
        ArgumentNullException.ThrowIfNull(eventSink);
        _eventSink = eventSink;
    }

    public void NotifyPublished(PublishedRelease release)
    {
        ArgumentNullException.ThrowIfNull(release);
        _notifications.Add("release-published:" + release.ReleaseId);
        _eventSink("notify:" + release.ReleaseId);
    }

    public IReadOnlyList<string> Notifications() => _notifications.ToList().AsReadOnly();
}
