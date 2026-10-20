import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import type { DeploymentStatus } from './DeploymentStatus.js';

export class DeploymentResult {
  readonly status: DeploymentStatus;
  readonly environment: string;
  readonly description: string;

  constructor(status: DeploymentStatus, environment: string, description: string) {
    this.status = requireNonNull(status, 'status');
    this.environment = DeploymentResult.normalized(environment, 'environment');
    this.description = DeploymentResult.normalized(description, 'description');
  }

  private static normalized(value: string, name: string): string {
    requireNonNull(value, name);
    const normalized = value.trim();
    if (normalized.length === 0) {
      throw new IllegalArgumentError(`${name} must not be blank`);
    }
    return normalized;
  }
}
