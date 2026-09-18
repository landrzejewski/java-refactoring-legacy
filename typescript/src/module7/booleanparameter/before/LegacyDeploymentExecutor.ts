import { IllegalArgumentError } from '../../../shared/errors.js';

export class LegacyDeploymentExecutor {
  execute(deploymentId: string, dryRun: boolean): string {
    LegacyDeploymentExecutor.validate(deploymentId);

    if (dryRun) {
      return 'preview:' + deploymentId;
    }
    return 'deployed:' + deploymentId;
  }

  private static validate(deploymentId: string | null): void {
    if (deploymentId == null || deploymentId.trim() === '') {
      throw new IllegalArgumentError('deploymentId must not be blank');
    }
  }
}
