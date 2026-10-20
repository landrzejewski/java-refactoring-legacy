import type { DeploymentCommand } from './DeploymentCommand.js';

export class PauseDeployment implements DeploymentCommand {
  execute(releaseId: string): string {
    return 'paused:' + releaseId;
  }
}
