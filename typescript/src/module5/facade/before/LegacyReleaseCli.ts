import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { isBlank } from '../../support.js';
import type { ArtifactStore } from './ArtifactStore.js';
import type { ConfigRenderer } from './ConfigRenderer.js';
import type { DeploymentQueue } from './DeploymentQueue.js';
import type { ReleaseNotifier } from './ReleaseNotifier.js';

/**
 * Klient sam orkiestruje cztery podsystemy i sam pilnuje kolejności oraz walidacji.
 * Ta sama wiedza jest skopiowana w LegacyReleaseScheduler.
 */
export class LegacyReleaseCli {
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

  publish(releaseId: string, environment: string): string {
    if (isBlank(releaseId)) {
      throw new IllegalArgumentError('releaseId must not be blank');
    }
    const target = environment.trim().toLowerCase();
    if (!new Set(['staging', 'prod']).has(target)) {
      throw new IllegalArgumentError('unknown environment: ' + environment);
    }
    const artifact = this.store.upload(releaseId);
    const config = this.renderer.render(releaseId, target);
    this.queue.enqueue(artifact, config);
    this.notifier.announce(releaseId, target);
    return 'published ' + releaseId + ' to ' + target;
  }
}
