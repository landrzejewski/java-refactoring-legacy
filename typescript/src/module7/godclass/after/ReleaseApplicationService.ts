import { IllegalStateError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { PublishedRelease } from '../PublishedRelease.js';
import type { AuditTrail } from './AuditTrail.js';
import type { ReleaseNotifier } from './ReleaseNotifier.js';
import type { ReleaseRepository } from './ReleaseRepository.js';
import type { ReleaseValidator } from './ReleaseValidator.js';

export class ReleaseApplicationService {
  private readonly validator: ReleaseValidator;
  private readonly repository: ReleaseRepository;
  private readonly auditTrail: AuditTrail;
  private readonly notifier: ReleaseNotifier;

  constructor(
    validator: ReleaseValidator,
    repository: ReleaseRepository,
    auditTrail: AuditTrail,
    notifier: ReleaseNotifier,
  ) {
    this.validator = requireNonNull(validator, 'validator');
    this.repository = requireNonNull(repository, 'repository');
    this.auditTrail = requireNonNull(auditTrail, 'auditTrail');
    this.notifier = requireNonNull(notifier, 'notifier');
  }

  publish(
    releaseId: string,
    service: string,
    version: string,
  ): PublishedRelease {
    const release = this.validator.validate(
      releaseId, service, version);
    if (this.repository.existsById(release.releaseId)) {
      throw new IllegalStateError(
        'release already published: ' + release.releaseId);
    }

    this.repository.save(release);
    this.auditTrail.recordPublished(release);
    this.notifier.notifyPublished(release);
    return release;
  }
}
