import { requireNonNull } from '../../../shared/requireNonNull.js';
import { addExact } from '../../ExactMath.js';
import type { DeploymentSample } from '../DeploymentSample.js';

export class LegacyDeploymentReport {
  generate(samples: readonly DeploymentSample[]): string {
    requireNonNull(samples, 'samples');

    let deployments = 0;
    let failures = 0;
    let totalLeadTimeMinutes = 0;

    for (const sample of samples) {
      requireNonNull(sample, 'sample');
      deployments++;
      if (!sample.successful) {
        failures++;
      }
      totalLeadTimeMinutes = addExact(
        totalLeadTimeMinutes, sample.leadTimeMinutes);
    }

    const averageLeadTimeMinutes = deployments === 0
      ? 0
      : Math.trunc(totalLeadTimeMinutes / deployments);

    return 'deployments=' + deployments
      + ';failures=' + failures
      + ';avgLeadTimeMinutes=' + averageLeadTimeMinutes;
  }
}
