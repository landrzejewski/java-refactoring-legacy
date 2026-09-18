import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { PublishedRelease } from '../PublishedRelease.js';

export class ReleaseNotifier {
  readonly #notifications: string[] = [];
  private readonly eventSink: (event: string) => void;

  constructor(eventSink: (event: string) => void = () => { }) {
    this.eventSink = requireNonNull(eventSink, 'eventSink');
  }

  notifyPublished(release: PublishedRelease): void {
    requireNonNull(release, 'release');
    this.#notifications.push('release-published:' + release.releaseId);
    this.eventSink('notify:' + release.releaseId);
  }

  notifications(): readonly string[] {
    return Object.freeze([...this.#notifications]);
  }
}
