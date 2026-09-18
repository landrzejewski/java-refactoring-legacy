import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { DeploymentDecision } from '../DeploymentDecision.js';
import { StandardMaintenanceWindows } from './StandardMaintenanceWindows.js';

export class LegacyDeploymentWindowService {
  // Twarda zależność tworzona wewnątrz klasy — brak szwu do testów.
  private readonly maintenanceWindows = new StandardMaintenanceWindows();

  schedule(service: string, hourUtc: number): DeploymentDecision {
    LegacyDeploymentWindowService.validate(service, hourUtc);
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
