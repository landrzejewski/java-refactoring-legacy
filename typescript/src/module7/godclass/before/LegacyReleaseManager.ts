import { IllegalArgumentError, IllegalStateError } from '../../../shared/errors.js';
import { PublishedRelease } from '../PublishedRelease.js';

export class LegacyReleaseManager {
  readonly #releases = new Map<string, PublishedRelease>();
  readonly #auditEntries: string[] = [];
  readonly #notifications: string[] = [];
  readonly #events: string[] = [];

  publish(
    releaseId: string,
    service: string,
    version: string,
  ): PublishedRelease {
    LegacyReleaseManager.requireText(releaseId, 'releaseId');
    LegacyReleaseManager.requireText(service, 'service');
    LegacyReleaseManager.requireText(version, 'version');
    if (this.#releases.has(releaseId)) {
      throw new IllegalStateError(
        'release already published: ' + releaseId);
    }

    const release = new PublishedRelease(releaseId, service, version);
    this.#releases.set(releaseId, release);
    this.#events.push('save:' + releaseId);
    this.#auditEntries.push('published:' + releaseId);
    this.#events.push('audit:' + releaseId);
    this.#notifications.push('release-published:' + releaseId);
    this.#events.push('notify:' + releaseId);
    return release;
  }

  releases(): readonly PublishedRelease[] {
    return Object.freeze([...this.#releases.values()]);
  }

  auditEntries(): readonly string[] {
    return Object.freeze([...this.#auditEntries]);
  }

  notifications(): readonly string[] {
    return Object.freeze([...this.#notifications]);
  }

  events(): readonly string[] {
    return Object.freeze([...this.#events]);
  }

  private static requireText(value: string | null, field: string): void {
    if (value == null || value.trim() === '') {
      throw new IllegalArgumentError(field + ' must not be blank');
    }
  }
}
