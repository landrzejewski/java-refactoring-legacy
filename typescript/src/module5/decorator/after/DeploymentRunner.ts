export interface DeploymentRunner {
  run(releaseId: string): string;
}
