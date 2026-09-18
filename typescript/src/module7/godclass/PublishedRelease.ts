export class PublishedRelease {
  constructor(
    readonly releaseId: string,
    readonly service: string,
    readonly version: string,
  ) {}
}
