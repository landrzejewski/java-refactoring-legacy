import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { DeploymentStatus } from '../DeploymentStatus.js';

export class DeploymentRegistry {
  private readonly statuses = new Map<string, DeploymentStatus>();

  update(deploymentId: string, status: DeploymentStatus): void {
    DeploymentRegistry.validateDeploymentId(deploymentId);
    this.statuses.set(
      deploymentId,
      requireNonNull(status, 'status must not be null'));
  }

  statusOf(deploymentId: string): DeploymentStatus {
    DeploymentRegistry.validateDeploymentId(deploymentId);
    return this.statuses.get(deploymentId) ?? DeploymentStatus.UNKNOWN;
  }

  private static validateDeploymentId(deploymentId: string | null): void {
    if (deploymentId == null || deploymentId.trim() === '') {
      throw new IllegalArgumentError('deploymentId must not be blank');
    }
  }
}
