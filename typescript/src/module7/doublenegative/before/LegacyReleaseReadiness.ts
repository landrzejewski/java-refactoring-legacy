export class LegacyReleaseReadiness {
  constructor(
    readonly notApproved: boolean,
    readonly testsNotPassed: boolean,
    readonly windowNotOpen: boolean,
  ) {}
}
