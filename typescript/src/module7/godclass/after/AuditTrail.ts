import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { PublishedRelease } from '../PublishedRelease.js';

export class AuditTrail {
  readonly #entries: string[] = [];
  private readonly eventSink: (event: string) => void;

  constructor(eventSink: (event: string) => void = () => { }) {
    this.eventSink = requireNonNull(eventSink, 'eventSink');
  }

  recordPublished(release: PublishedRelease): void {
    requireNonNull(release, 'release');
    this.#entries.push('published:' + release.releaseId);
    this.eventSink('audit:' + release.releaseId);
  }

  entries(): readonly string[] {
    return Object.freeze([...this.#entries]);
  }
}
