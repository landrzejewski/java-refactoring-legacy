/**
 * Migawka formularza wydania - wspólna dla wersji przed i po refaktoryzacji.
 */
export class FormState {
  constructor(
    readonly version: string,
    readonly versionEditable: boolean,
    readonly approverEnabled: boolean,
    readonly approver: string,
    readonly publishEnabled: boolean,
    readonly status: string,
  ) {}
}
