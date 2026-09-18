export interface DeploymentCommand {
  execute(releaseId: string): string;
}
