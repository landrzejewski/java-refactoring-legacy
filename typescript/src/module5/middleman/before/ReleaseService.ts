import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { DeploymentStatus } from '../DeploymentStatus.js';
import type { DeploymentRegistry } from './DeploymentRegistry.js';

// Pośrednik (Middle Man): każda metoda tylko deleguje do rejestru.
export class ReleaseService {
  private readonly registry: DeploymentRegistry;

  constructor(registry: DeploymentRegistry) {
    this.registry = requireNonNull(
      registry, 'registry must not be null');
  }

  update(deploymentId: string, status: DeploymentStatus): void {
    this.registry.update(deploymentId, status);
  }

  statusOf(deploymentId: string): DeploymentStatus {
    return this.registry.statusOf(deploymentId);
  }
}
