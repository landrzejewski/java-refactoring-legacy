import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { DeploymentRegistry } from './DeploymentRegistry.js';

export class ReleaseDashboard {
  private readonly registry: DeploymentRegistry;

  constructor(registry: DeploymentRegistry) {
    this.registry = requireNonNull(
      registry, 'registry must not be null');
  }

  render(deploymentId: string): string {
    return deploymentId + ' -> ' + this.registry.statusOf(deploymentId);
  }
}
