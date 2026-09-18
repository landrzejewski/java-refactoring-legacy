export class ManifestEntry {
  constructor(
    readonly artifact: string,
    readonly checksum: string,
    readonly deploymentOrder: number,
  ) {}
}
