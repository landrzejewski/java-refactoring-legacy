import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { ManifestEntry } from '../ManifestEntry.js';

export class ReleaseManifestBuilder {
  build(entries: readonly ManifestEntry[]): string {
    const validatedEntries = this.validateAndCopy(entries);
    const orderedEntries = this.order(validatedEntries);
    return this.render(orderedEntries);
  }

  private validateAndCopy(entries: readonly ManifestEntry[]): ManifestEntry[] {
    requireNonNull(entries, 'entries');

    const copy: ManifestEntry[] = [];
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
      copy.push(entry);
    }
    return copy;
  }

  private order(entries: readonly ManifestEntry[]): ManifestEntry[] {
    // toSorted() nie modyfikuje wejścia i jest stabilne (jak Stream.sorted()).
    return entries.toSorted((a, b) =>
      a.deploymentOrder - b.deploymentOrder
      || compareStrings(a.artifact, b.artifact));
  }

  private render(entries: readonly ManifestEntry[]): string {
    return entries
      .map(entry =>
        entry.deploymentOrder
        + '|'
        + entry.artifact
        + '|'
        + entry.checksum)
      .join('\n');
  }
}

// Porównanie po jednostkach UTF-16, jak String.compareTo w Javie
// (niezależne od locale — bez localeCompare).
function compareStrings(a: string, b: string): number {
  return a < b ? -1 : a > b ? 1 : 0;
}
