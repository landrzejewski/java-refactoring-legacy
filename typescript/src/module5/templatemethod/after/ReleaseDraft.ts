export class ReleaseDraft {
  constructor(
    readonly releaseId: string,
    readonly service: string,
  ) {}

  // Odpowiednik toString() rekordu Javy.
  toString(): string {
    return `ReleaseDraft[releaseId=${this.releaseId}, service=${this.service}]`;
  }
}
