import { requireNonNull } from '../../../shared/requireNonNull.js';

export class ArtifactStore {
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
