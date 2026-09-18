import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { Artifact } from '../Artifact.js';

export class LegacyArtifactFinder {
  findByChecksum(
    artifacts: readonly Artifact[], checksum: string): Artifact | undefined {
    requireNonNull(artifacts, 'artifacts must not be null');
    requireNonNull(checksum, 'checksum must not be null');

    let result: Artifact | null = null;
    let index = 0;
    while (result == null && index < artifacts.length) {
      const artifact = requireNonNull(
        artifacts[index], 'artifact must not be null');
      if (checksum === artifact.checksum) {
        result = artifact;
      }
      index++;
    }

    return result ?? undefined;
  }
}
