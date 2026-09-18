namespace Training.Module7.GodClass.Before;

public sealed class LegacyReleaseManager
{
    private readonly OrderedDictionary<string, PublishedRelease> _releases = [];
    private readonly List<string> _auditEntries = [];
    private readonly List<string> _notifications = [];
    private readonly List<string> _events = [];

    public PublishedRelease Publish(string releaseId, string service, string version)
    {
        RequireText(releaseId, "releaseId");
        RequireText(service, "service");
        RequireText(version, "version");
        if (_releases.ContainsKey(releaseId))
        {
            throw new InvalidOperationException(
                "release already published: " + releaseId);
        }

        var release = new PublishedRelease(releaseId, service, version);
        _releases.Add(releaseId, release);
        _events.Add("save:" + releaseId);
        _auditEntries.Add("published:" + releaseId);
        _events.Add("audit:" + releaseId);
        _notifications.Add("release-published:" + releaseId);
        _events.Add("notify:" + releaseId);
        return release;
    }

    public IReadOnlyList<PublishedRelease> Releases() => _releases.Values.ToList().AsReadOnly();

    public IReadOnlyList<string> AuditEntries() => _auditEntries.ToList().AsReadOnly();

    public IReadOnlyList<string> Notifications() => _notifications.ToList().AsReadOnly();

    public IReadOnlyList<string> Events() => _events.ToList().AsReadOnly();

    private static void RequireText(string? value, string field)
    {
        if (string.IsNullOrWhiteSpace(value))
        {
            throw new ArgumentException(field + " must not be blank");
        }
    }
}
