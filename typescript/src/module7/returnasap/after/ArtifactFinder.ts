import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { Artifact } from '../Artifact.js';

export class ArtifactFinder {
  // Optional<Artifact> → Artifact | undefined
  findByChecksum(
    artifacts: readonly Artifact[], checksum: string): Artifact | undefined {
    requireNonNull(artifacts, 'artifacts must not be null');
    requireNonNull(checksum, 'checksum must not be null');

    for (let index = 0; index < artifacts.length; index++) {
      const artifact = requireNonNull(
        artifacts[index], 'artifact must not be null');
      if (checksum === artifact.checksum) {
        return artifact;
      }
    }

    return undefined;
  }
}
