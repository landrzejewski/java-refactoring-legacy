import { IllegalArgumentError } from '../../shared/errors.js';
import { Duration } from '../support.js';

// Odpowiednik `enum DeploymentDefaults { INSTANCE; }`: prywatny konstruktor
// i jedyna instancja tworzona przy ładowaniu modułu (ESM ładuje moduł raz,
// JS jest jednowątkowy — nie potrzeba synchronizacji).
export class DeploymentDefaults {
  static readonly INSTANCE = new DeploymentDefaults();

  private readonly timeout = Duration.ofSeconds(30);

  private constructor() {}

  // Odpowiednik Enum.valueOf(String).
  static valueOf(name: string): DeploymentDefaults {
    if (name === 'INSTANCE') {
      return DeploymentDefaults.INSTANCE;
    }
    throw new IllegalArgumentError('No enum constant DeploymentDefaults.' + name);
  }

  healthCheckTimeout(): Duration {
    return this.timeout;
  }
}
