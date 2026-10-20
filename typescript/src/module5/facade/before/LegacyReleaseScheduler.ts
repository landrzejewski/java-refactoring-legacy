import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { isBlank } from '../../support.js';
import type { ArtifactStore } from './ArtifactStore.js';
import type { ConfigRenderer } from './ConfigRenderer.js';
import type { DeploymentQueue } from './DeploymentQueue.js';
import type { ReleaseNotifier } from './ReleaseNotifier.js';

/**
 * Drugi klient z tą samą orkiestracją, skopiowaną i nieco inaczej zapisaną.
 * Zmiana kolejności kroków w jednym miejscu łatwo rozjedzie się z drugim.
 */
export class LegacyReleaseScheduler {
  private readonly store: ArtifactStore;
  private readonly renderer: ConfigRenderer;
  private readonly queue: DeploymentQueue;
  private readonly notifier: ReleaseNotifier;

  constructor(store: ArtifactStore, renderer: ConfigRenderer, queue: DeploymentQueue, notifier: ReleaseNotifier) {
    this.store = requireNonNull(store, 'store');
    this.renderer = requireNonNull(renderer, 'renderer');
    this.queue = requireNonNull(queue, 'queue');
    this.notifier = requireNonNull(notifier, 'notifier');
  }

  publishNightly(releaseIds: readonly string[]): string[] {
    const published: string[] = [];
    for (const releaseId of releaseIds) {
      if (isBlank(releaseId)) {
        throw new IllegalArgumentError('releaseId must not be blank');
      }
      const artifact = this.store.upload(releaseId);
      const config = this.renderer.render(releaseId, 'staging');
      this.queue.enqueue(artifact, config);
      this.notifier.announce(releaseId, 'staging');
      published.push('published ' + releaseId + ' to staging');
    }
    return published;
  }
}
