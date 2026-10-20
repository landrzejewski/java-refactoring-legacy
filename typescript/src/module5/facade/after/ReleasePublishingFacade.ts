import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { isBlank } from '../../support.js';

// W Javie cztery podsystemy (ArtifactStore, ConfigRenderer, DeploymentQueue, ReleaseNotifier)
// to osobne pliki z klasami pakietowo-prywatnymi. TypeScript nie ma widoczności pakietowej,
// więc przeniesione są tutaj, obok fasady, i nie są eksportowane - poza tym modułem
// nie da się ich zaimportować ani złożyć w złej kolejności.

class ArtifactStore {
  private readonly effects: (effect: string) => void;

  constructor(effects: (effect: string) => void) {
    this.effects = requireNonNull(effects, 'effects');
  }

  upload(releaseId: string): string {
    const artifact = releaseId + '.jar';
    this.effects('stored ' + artifact);
    return artifact;
  }
}

class ConfigRenderer {
  private readonly effects: (effect: string) => void;

  constructor(effects: (effect: string) => void) {
    this.effects = requireNonNull(effects, 'effects');
  }

  render(releaseId: string, environment: string): string {
    const config = releaseId + '/' + environment + '.yaml';
    this.effects('rendered ' + config);
    return config;
  }
}

class DeploymentQueue {
  private readonly effects: (effect: string) => void;

  constructor(effects: (effect: string) => void) {
    this.effects = requireNonNull(effects, 'effects');
  }

  enqueue(artifact: string, config: string): void {
    this.effects('queued ' + artifact + ' with ' + config);
  }
}

class ReleaseNotifier {
  private readonly effects: (effect: string) => void;

  constructor(effects: (effect: string) => void) {
    this.effects = requireNonNull(effects, 'effects');
  }

  announce(releaseId: string, environment: string): void {
    this.effects('announced ' + releaseId + ' on ' + environment);
  }
}

const ENVIRONMENTS: ReadonlySet<string> = new Set(['staging', 'prod']);

/**
 * Fasada jako strażnik spójności: jedna operacja wymusza kolejność kroków i walidację.
 * Podsystemy są prywatne dla modułu, więc klient nie może ich złożyć
 * w złej kolejności ani pominąć kroku.
 */
export class ReleasePublishingFacade {
  private readonly store: ArtifactStore;
  private readonly renderer: ConfigRenderer;
  private readonly queue: DeploymentQueue;
  private readonly notifier: ReleaseNotifier;

  constructor(effects: (effect: string) => void) {
    this.store = new ArtifactStore(effects);
    this.renderer = new ConfigRenderer(effects);
    this.queue = new DeploymentQueue(effects);
    this.notifier = new ReleaseNotifier(effects);
  }

  publish(releaseId: string, environment: string): string {
    if (isBlank(releaseId)) {
      throw new IllegalArgumentError('releaseId must not be blank');
    }
    requireNonNull(environment, 'environment');
    const target = environment.trim().toLowerCase();
    if (!ENVIRONMENTS.has(target)) {
      throw new IllegalArgumentError('unknown environment: ' + environment);
    }
    const artifact = this.store.upload(releaseId);
    const config = this.renderer.render(releaseId, target);
    this.queue.enqueue(artifact, config);
    this.notifier.announce(releaseId, target);
    return 'published ' + releaseId + ' to ' + target;
  }
}
