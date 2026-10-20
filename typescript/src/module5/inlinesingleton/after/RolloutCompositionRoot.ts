import { FeatureFlags } from './FeatureFlags.js';
import { RolloutService } from './RolloutService.js';

/**
 * Korzeń kompozycji: jedyne miejsce, które decyduje, ile instancji flag istnieje.
 * "Jedna instancja" przestaje być własnością klasy, a staje się decyzją konfiguracji.
 */
export class RolloutCompositionRoot {
  private constructor() {}

  static create(flagsConfig: string): RolloutService {
    return new RolloutService(FeatureFlags.parse(flagsConfig));
  }
}
