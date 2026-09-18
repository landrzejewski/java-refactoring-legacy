import { requireNonNull } from '../../../shared/requireNonNull.js';
import { simpleName } from '../../support.js';
import type { DeploymentRunner } from './DeploymentRunner.js';

export class AuditedDeploymentRunner implements DeploymentRunner {
  private readonly delegate: DeploymentRunner;
  private readonly audit: (entry: string) => void;

  constructor(delegate: DeploymentRunner, audit: (entry: string) => void) {
    this.delegate = requireNonNull(delegate, 'delegate');
    this.audit = requireNonNull(audit, 'audit');
  }

  run(releaseId: string): string {
    this.audit('start:' + releaseId);
    try {
      const result = this.delegate.run(releaseId);
      this.audit('success:' + releaseId);
      return result;
    } catch (exception) {
      this.audit('failure:' + releaseId + ':' + simpleName(exception));
      throw exception;
    }
  }
}
