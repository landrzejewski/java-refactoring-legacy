import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';

export class LegacyArtifactPublisher {
  publishSnapshot(artifactName: string, buildNumber: number): string {
    requireNonNull(artifactName, 'artifactName');
    if (artifactName.trim() === '') {
      throw new IllegalArgumentError('artifactName must not be blank');
    }
    if (buildNumber <= 0) {
      throw new IllegalArgumentError(
        'buildNumber must be greater than zero');
    }

    const normalizedName = artifactName.trim().toLowerCase();
    return normalizedName + ':' + buildNumber + '-SNAPSHOT';
  }

  publishRelease(artifactName: string, buildNumber: number): string {
    requireNonNull(artifactName, 'artifactName');
    if (artifactName.trim() === '') {
      throw new IllegalArgumentError('artifactName must not be blank');
    }
    if (buildNumber <= 0) {
      throw new IllegalArgumentError(
        'buildNumber must be greater than zero');
    }

    const normalizedName = artifactName.trim().toLowerCase();
    return normalizedName + ':' + buildNumber;
  }
}
