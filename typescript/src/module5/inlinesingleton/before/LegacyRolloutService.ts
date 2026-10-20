import { IllegalArgumentError } from '../../../shared/errors.js';
import { isBlank } from '../../support.js';
import { LegacyFeatureFlags } from './LegacyFeatureFlags.js';

/**
 * Ukryta zależność: z sygnatury nie widać, że wynik zależy od globalnych flag.
 */
export class LegacyRolloutService {
  plan(releaseId: string): string {
    if (isBlank(releaseId)) {
      throw new IllegalArgumentError('releaseId must not be blank');
    }
    const flags = LegacyFeatureFlags.getInstance();
    const stages = flags.isEnabled('canary') ? '10% -> 50% -> 100%' : '100%';
    const suffix = flags.isEnabled('dark-launch') ? ' (dark launch)' : '';
    return releaseId + ': ' + stages + suffix;
  }
}
