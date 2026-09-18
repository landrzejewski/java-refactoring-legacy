import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { DeploymentMetrics } from '../DeploymentMetrics.js';

export class DeploymentReportFormatter {
  format(metrics: DeploymentMetrics): string {
    requireNonNull(metrics, 'metrics');
    return 'deployments=' + metrics.deployments
      + ';failures=' + metrics.failures
      + ';avgLeadTimeMinutes='
      + metrics.averageLeadTimeMinutes;
  }
}
