namespace Training.Module6.Observer.Before;

public sealed class LegacyReleasePublisher
{
    private readonly Action<PublishedRelease> _auditLog;
    private readonly Action<PublishedRelease> _metrics;

    public LegacyReleasePublisher(
        Action<PublishedRelease> auditLog,
        Action<PublishedRelease> metrics)
    {
        ArgumentNullException.ThrowIfNull(auditLog);
        ArgumentNullException.ThrowIfNull(metrics);
        _auditLog = auditLog;
        _metrics = metrics;
    }

    public void Publish(PublishedRelease release)
    {
        ArgumentNullException.ThrowIfNull(release);
        _auditLog(release);
        _metrics(release);
    }

    public sealed record PublishedRelease
    {
        public PublishedRelease(string? releaseId)
        {
            if (string.IsNullOrWhiteSpace(releaseId))
            {
                throw new ArgumentException("releaseId must not be blank");
            }
            ReleaseId = releaseId;
        }

        public string ReleaseId { get; }
    }
}
