import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { isBlank, type LocalDate } from '../../support.js';

// Odpowiednik zagnieżdżonego enuma Environment.Tier.
export enum Tier {
  PRODUCTION = 'PRODUCTION',
  TEST = 'TEST',
  DEVELOPMENT = 'DEVELOPMENT',
}

/**
 * Model domenowy środowiska - nazwany w języku planowania wdrożeń, nie CMDB.
 */
export class Environment {
  constructor(
    readonly name: string,
    readonly tier: Tier,
    readonly active: boolean,
    readonly lastDeployment: LocalDate,
  ) {
    if (isBlank(name)) {
      throw new IllegalArgumentError('name must not be blank');
    }
    requireNonNull(tier, 'tier');
    requireNonNull(lastDeployment, 'lastDeployment');
  }

  // Odpowiednik equals() rekordu Javy.
  equals(other: unknown): boolean {
    return other instanceof Environment
      && this.name === other.name
      && this.tier === other.tier
      && this.active === other.active
      && this.lastDeployment.equals(other.lastDeployment);
  }
}
