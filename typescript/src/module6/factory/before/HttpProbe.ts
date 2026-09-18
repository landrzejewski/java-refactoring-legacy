import { IllegalArgumentError } from '../../../shared/errors.js';
import { isBlank } from '../../support.js';
import type { DeploymentProbe } from './DeploymentProbe.js';

export class HttpProbe implements DeploymentProbe {
  constructor(readonly endpoint: string) {
    if (isBlank(endpoint)) {
      throw new IllegalArgumentError('endpoint must not be blank');
    }
  }

  check(): string {
    return 'http-ok:' + this.endpoint;
  }
}
