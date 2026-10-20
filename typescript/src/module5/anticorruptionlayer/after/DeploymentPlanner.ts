import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { LocalDate } from '../../support.js';
import { Tier } from './Environment.js';
import type { EnvironmentDirectory } from './EnvironmentDirectory.js';

/**
 * Reguły planowania wyrażone wyłącznie modelem domenowym.
 */
export class DeploymentPlanner {
  private readonly environments: EnvironmentDirectory;

  constructor(environments: EnvironmentDirectory) {
    this.environments = requireNonNull(environments, 'environments');
  }

  canaryTargets(): readonly string[] {
    return Object.freeze(
      this.environments.all()
        .filter(environment => environment.active)
        .filter(environment => environment.tier !== Tier.PRODUCTION)
        .map(environment => environment.name)
        .sort(),
    );
  }

  staleEnvironments(today: LocalDate): readonly string[] {
    const threshold = today.minusDays(30);
    return Object.freeze(
      this.environments.all()
        .filter(environment => environment.active)
        .filter(environment => environment.lastDeployment.isBefore(threshold))
        .map(environment => environment.name)
        .sort(),
    );
  }

  isProduction(name: string): boolean {
    return this.environments.all()
      .some(environment => environment.name === name && environment.tier === Tier.PRODUCTION);
  }
}
