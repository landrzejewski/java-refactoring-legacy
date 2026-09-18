import type { DeploymentCommand } from './DeploymentCommand.js';

export class RollbackDeployment implements DeploymentCommand {
  execute(releaseId: string): string {
    return 'rolled-back:' + releaseId;
  }
}
