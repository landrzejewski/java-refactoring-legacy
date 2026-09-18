import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { DeploymentResult } from '../DeploymentResult.js';
import { DeploymentStatus } from '../DeploymentStatus.js';

export class ReleaseSummaryFormatter {
  format(releaseId: string, results: readonly DeploymentResult[]): string {
    requireNonNull(releaseId, 'releaseId');
    requireNonNull(results, 'results');
    if (releaseId.trim().length === 0) {
      throw new IllegalArgumentError('releaseId must not be blank');
    }
    if (results.length === 0) {
      throw new IllegalArgumentError('results must not be empty');
    }

    let s = 'Release ' + releaseId.trim() + '\n';
    let n = 0;
    for (const r of results) {
      requireNonNull(r, 'result');
      if (r.status === DeploymentStatus.SUCCESS) {
        s = s + '[OK] ' + r.environment + ': ' + r.description + '\n';
        n++;
      } else {
        s = s + '[ERROR] ' + r.environment + ': ' + r.description + '\n';
      }
    }
    return s + 'Successful: ' + n + '/' + results.length;
  }
}
