import { requireNonNull } from '../../../shared/requireNonNull.js';

export class ReleaseNotifier {
  private readonly effects: (effect: string) => void;

  constructor(effects: (effect: string) => void) {
    this.effects = requireNonNull(effects, 'effects');
  }

  announce(releaseId: string, environment: string): void {
    this.effects('announced ' + releaseId + ' on ' + environment);
  }
}
