import { IllegalArgumentError } from '../../../shared/errors.js';
import { isBlank } from '../../support.js';
import type { DeploymentRunner } from './DeploymentRunner.js';

export class BasicDeploymentRunner implements DeploymentRunner {
  run(releaseId: string): string {
    if (isBlank(releaseId)) {
      throw new IllegalArgumentError('releaseId must not be blank');
    }
    return 'deployed:' + releaseId;
  }
}
