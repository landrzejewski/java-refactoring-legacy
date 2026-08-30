package pl.training.module7.godclass.after;

import java.util.Objects;

import pl.training.module7.godclass.PublishedRelease;

public final class ReleaseApplicationService {
    private final ReleaseValidator validator;
    private final ReleaseRepository repository;
    private final AuditTrail auditTrail;
    private final ReleaseNotifier notifier;

    public ReleaseApplicationService(
            ReleaseValidator validator,
            ReleaseRepository repository,
            AuditTrail auditTrail,
            ReleaseNotifier notifier) {
        this.validator = Objects.requireNonNull(validator, "validator");
        this.repository = Objects.requireNonNull(repository, "repository");
        this.auditTrail = Objects.requireNonNull(auditTrail, "auditTrail");
        this.notifier = Objects.requireNonNull(notifier, "notifier");
    }

    public PublishedRelease publish(
            String releaseId,
            String service,
            String version) {
        PublishedRelease release = validator.validate(
                releaseId, service, version);
        if (repository.existsById(release.releaseId())) {
            throw new IllegalStateException(
                    "release already published: " + release.releaseId());
        }

        repository.save(release);
        auditTrail.recordPublished(release);
        notifier.notifyPublished(release);
        return release;
    }
}
