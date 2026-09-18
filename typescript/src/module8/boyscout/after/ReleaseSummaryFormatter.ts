import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { DeploymentResult } from '../DeploymentResult.js';
import { DeploymentStatus } from '../DeploymentStatus.js';

export class ReleaseSummaryFormatter {
  format(releaseId: string, results: readonly DeploymentResult[]): string {
    ReleaseSummaryFormatter.validate(releaseId, results);

    const summary: string[] = [`Release ${releaseId.trim()}\n`];
    let successfulDeployments = 0;
    for (const result of results) {
      requireNonNull(result, 'result');
      summary.push(ReleaseSummaryFormatter.formatResult(result), '\n');
      if (result.status === DeploymentStatus.SUCCESS) {
        successfulDeployments++;
      }
    }
    summary.push(`Successful: ${successfulDeployments}/${results.length}`);
    return summary.join('');
  }

  private static validate(releaseId: string, results: readonly DeploymentResult[]): void {
    requireNonNull(releaseId, 'releaseId');
    requireNonNull(results, 'results');
    if (releaseId.trim().length === 0) {
      throw new IllegalArgumentError('releaseId must not be blank');
    }
    if (results.length === 0) {
      throw new IllegalArgumentError('results must not be empty');
    }
  }

  private static formatResult(result: DeploymentResult): string {
    const label = result.status === DeploymentStatus.SUCCESS ? '[OK]' : '[ERROR]';
    return `${label} ${result.environment}: ${result.description}`;
  }
}
