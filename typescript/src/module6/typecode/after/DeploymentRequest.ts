import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { isBlank } from '../../support.js';
import type { DeploymentZone } from './DeploymentZone.js';

export class DeploymentRequest {
  constructor(
    readonly releaseId: string,
    readonly zone: DeploymentZone,
  ) {
    if (isBlank(releaseId)) {
      throw new IllegalArgumentError('releaseId must not be blank');
    }
    requireNonNull(zone, 'zone');
  }

  requiresApproval(): boolean {
    return this.zone.requiresApproval();
  }
}
