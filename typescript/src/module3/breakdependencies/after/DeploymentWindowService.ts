import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { DeploymentDecision } from '../DeploymentDecision.js';
import type { MaintenanceWindows } from './MaintenanceWindows.js';

export class DeploymentWindowService {
  private readonly maintenanceWindows: MaintenanceWindows;

  constructor(maintenanceWindows: MaintenanceWindows) {
    this.maintenanceWindows = requireNonNull(
      maintenanceWindows, 'maintenanceWindows');
  }

  schedule(service: string, hourUtc: number): DeploymentDecision {
    DeploymentWindowService.validate(service, hourUtc);
    return this.maintenanceWindows.allows(service, hourUtc)
      ? DeploymentDecision.ALLOWED
      : DeploymentDecision.OUTSIDE_MAINTENANCE_WINDOW;
  }

  private static validate(service: string, hourUtc: number): void {
    requireNonNull(service, 'service');
    if (service.trim() === '') {
      throw new IllegalArgumentError('service must not be blank');
    }
    if (hourUtc < 0 || hourUtc > 23) {
      throw new IllegalArgumentError(
        'hourUtc must be between 0 and 23');
    }
  }
}
