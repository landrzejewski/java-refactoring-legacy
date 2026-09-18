import { requireNonNull } from '../../../shared/requireNonNull.js';
import { addExact } from '../../ExactMath.js';
import { DeploymentMetrics } from '../DeploymentMetrics.js';
import type { DeploymentSample } from '../DeploymentSample.js';

export class DeploymentMetricsCalculator {
  calculate(samples: readonly DeploymentSample[]): DeploymentMetrics {
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

    return new DeploymentMetrics(
      deployments, failures, averageLeadTimeMinutes);
  }
}
