import { requireNonNull } from '../../../shared/requireNonNull.js';

export class ConfigRenderer {
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
