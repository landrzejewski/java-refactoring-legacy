// Java record DeploymentCandidate. releaseId może być null — przykład
// „before/after” jawnie obsługuje brakujący identyfikator.
export class DeploymentCandidate {
  constructor(
    readonly releaseId: string | null,
    readonly approved: boolean,
    readonly testsPassed: boolean,
    readonly windowOpen: boolean,
  ) {}
}
