export class ReleaseReadiness {
  constructor(
    readonly approved: boolean,
    readonly testsPassed: boolean,
    readonly windowOpen: boolean,
  ) {}
}
