import { IllegalArgumentError } from '../../../shared/errors.js';

// Odpowiednik prywatnego enuma ExecutionMode z Javy — niewidoczny poza modułem.
const ExecutionMode = {
  PREVIEW: { resultPrefix: 'preview:' },
  DEPLOY: { resultPrefix: 'deployed:' },
} as const;

type ExecutionMode = (typeof ExecutionMode)[keyof typeof ExecutionMode];

export class DeploymentExecutor {
  preview(deploymentId: string): string {
    return this.#execute(deploymentId, ExecutionMode.PREVIEW);
  }

  deploy(deploymentId: string): string {
    return this.#execute(deploymentId, ExecutionMode.DEPLOY);
  }

  // Prywatne metody ES (#) nie trafiają na prototyp — publiczne API
  // to wyłącznie preview() i deploy().
  #execute(deploymentId: string, mode: ExecutionMode): string {
    DeploymentExecutor.#validate(deploymentId);
    return mode.resultPrefix + deploymentId;
  }

  static #validate(deploymentId: string | null): void {
    if (deploymentId == null || deploymentId.trim() === '') {
      throw new IllegalArgumentError('deploymentId must not be blank');
    }
  }
}
