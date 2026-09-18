import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { ManifestEntry } from '../ManifestEntry.js';

export class LegacyReleaseManifestBuilder {
  build(entries: readonly ManifestEntry[]): string {
    requireNonNull(entries, 'entries');

    const validatedEntries: ManifestEntry[] = [];
    for (const entry of entries) {
      requireNonNull(entry, 'entries must not contain null');
      requireNonNull(entry.artifact, 'artifact');
      if (entry.artifact.trim() === '') {
        throw new IllegalArgumentError('artifact must not be blank');
      }
      requireNonNull(entry.checksum, 'checksum');
      if (entry.checksum.trim() === '') {
        throw new IllegalArgumentError('checksum must not be blank');
      }
      if (entry.deploymentOrder < 0) {
        throw new IllegalArgumentError(
          'deploymentOrder must not be negative');
      }
      validatedEntries.push(entry);
    }

    validatedEntries.sort((a, b) =>
      a.deploymentOrder - b.deploymentOrder
      || (a.artifact < b.artifact ? -1 : a.artifact > b.artifact ? 1 : 0));

    const manifest: string[] = [];
    for (const entry of validatedEntries) {
      manifest.push(
        entry.deploymentOrder
        + '|'
        + entry.artifact
        + '|'
        + entry.checksum);
    }
    return manifest.join('\n');
  }
}
