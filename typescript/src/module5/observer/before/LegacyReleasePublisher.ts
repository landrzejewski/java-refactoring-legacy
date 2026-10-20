import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { isBlank } from '../../support.js';

// Odpowiednik rekordu zagnieżdżonego LegacyReleasePublisher.PublishedRelease.
export class PublishedRelease {
  constructor(readonly releaseId: string) {
    if (isBlank(releaseId)) {
      throw new IllegalArgumentError('releaseId must not be blank');
    }
  }
}

export class LegacyReleasePublisher {
  private readonly auditLog: (release: PublishedRelease) => void;
  private readonly metrics: (release: PublishedRelease) => void;

  constructor(
    auditLog: (release: PublishedRelease) => void,
    metrics: (release: PublishedRelease) => void,
  ) {
    this.auditLog = requireNonNull(auditLog, 'auditLog');
    this.metrics = requireNonNull(metrics, 'metrics');
  }

  publish(release: PublishedRelease): void {
    this.auditLog(requireNonNull(release, 'release'));
    this.metrics(release);
  }
}
