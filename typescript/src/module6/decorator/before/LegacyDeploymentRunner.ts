import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { isBlank, simpleName } from '../../support.js';

export class LegacyDeploymentRunner {
  private readonly audit: (entry: string) => void;

  constructor(audit: (entry: string) => void) {
    this.audit = requireNonNull(audit, 'audit');
  }

  run(releaseId: string): string {
    this.audit('start:' + releaseId);
    try {
      if (isBlank(releaseId)) {
        throw new IllegalArgumentError('releaseId must not be blank');
      }
      const result = 'deployed:' + releaseId;
      this.audit('success:' + releaseId);
      return result;
    } catch (exception) {
      this.audit('failure:' + releaseId + ':' + simpleName(exception));
      throw exception;
    }
  }
}
