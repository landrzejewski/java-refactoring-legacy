import { IllegalArgumentError } from '../../../shared/errors.js';
import { isBlank } from '../../support.js';
import type { DeploymentProbe } from './DeploymentProbe.js';

export class QueueProbe implements DeploymentProbe {
  constructor(readonly queueName: string) {
    if (isBlank(queueName)) {
      throw new IllegalArgumentError('queueName must not be blank');
    }
  }

  check(): string {
    return 'queue-ok:' + this.queueName;
  }
}
