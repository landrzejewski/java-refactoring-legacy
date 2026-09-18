import { Duration } from '../../support.js';

// Każde `new` tworzy osobną instancję tych samych ustawień domyślnych.
export class LegacyDeploymentDefaults {
  private readonly timeout = Duration.ofSeconds(30);

  healthCheckTimeout(): Duration {
    return this.timeout;
  }
}
