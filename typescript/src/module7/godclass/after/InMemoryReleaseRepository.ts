import { IllegalStateError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { PublishedRelease } from '../PublishedRelease.js';
import type { ReleaseRepository } from './ReleaseRepository.js';

export class InMemoryReleaseRepository implements ReleaseRepository {
  // Map zachowuje kolejność wstawiania (jak LinkedHashMap).
  private readonly releases = new Map<string, PublishedRelease>();
  private readonly eventSink: (event: string) => void;

  constructor(eventSink: (event: string) => void = () => { }) {
    this.eventSink = requireNonNull(eventSink, 'eventSink');
  }

  existsById(releaseId: string): boolean {
    return this.releases.has(releaseId);
  }

  save(release: PublishedRelease): void {
    requireNonNull(release, 'release');
    if (this.releases.has(release.releaseId)) {
      throw new IllegalStateError(
        'release already published: ' + release.releaseId);
    }
    this.releases.set(release.releaseId, release);
    this.eventSink('save:' + release.releaseId);
  }

  findAll(): readonly PublishedRelease[] {
    return Object.freeze([...this.releases.values()]);
  }
}
