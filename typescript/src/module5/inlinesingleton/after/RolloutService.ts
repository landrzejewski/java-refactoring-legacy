import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { isBlank } from '../../support.js';
import type { FeatureFlags } from './FeatureFlags.js';

export class RolloutService {
  private readonly flags: FeatureFlags;

  constructor(flags: FeatureFlags) {
    this.flags = requireNonNull(flags, 'flags');
  }

  plan(releaseId: string): string {
    if (isBlank(releaseId)) {
      throw new IllegalArgumentError('releaseId must not be blank');
    }
    const stages = this.flags.isEnabled('canary') ? '10% -> 50% -> 100%' : '100%';
    const suffix = this.flags.isEnabled('dark-launch') ? ' (dark launch)' : '';
    return releaseId + ': ' + stages + suffix;
  }
}
