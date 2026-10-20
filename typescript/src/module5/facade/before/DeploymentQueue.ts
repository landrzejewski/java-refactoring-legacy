import { requireNonNull } from '../../../shared/requireNonNull.js';

export class DeploymentQueue {
  private readonly effects: (effect: string) => void;

  constructor(effects: (effect: string) => void) {
    this.effects = requireNonNull(effects, 'effects');
  }

  enqueue(artifact: string, config: string): void {
    this.effects('queued ' + artifact + ' with ' + config);
  }
}
