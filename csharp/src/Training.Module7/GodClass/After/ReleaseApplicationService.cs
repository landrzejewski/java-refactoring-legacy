namespace Training.Module7.GodClass.After;

public sealed class ReleaseApplicationService
{
    private readonly ReleaseValidator _validator;
    private readonly IReleaseRepository _repository;
    private readonly AuditTrail _auditTrail;
    private readonly ReleaseNotifier _notifier;

    public ReleaseApplicationService(
        ReleaseValidator validator,
        IReleaseRepository repository,
        AuditTrail auditTrail,
        ReleaseNotifier notifier)
    {
        ArgumentNullException.ThrowIfNull(validator);
        ArgumentNullException.ThrowIfNull(repository);
        ArgumentNullException.ThrowIfNull(auditTrail);
        ArgumentNullException.ThrowIfNull(notifier);
        _validator = validator;
        _repository = repository;
        _auditTrail = auditTrail;
        _notifier = notifier;
    }

    public PublishedRelease Publish(string releaseId, string service, string version)
    {
        var release = _validator.Validate(releaseId, service, version);
        if (_repository.ExistsById(release.ReleaseId))
        {
            throw new InvalidOperationException(
                "release already published: " + release.ReleaseId);
        }

        _repository.Save(release);
        _auditTrail.RecordPublished(release);
        _notifier.NotifyPublished(release);
        return release;
    }
}
