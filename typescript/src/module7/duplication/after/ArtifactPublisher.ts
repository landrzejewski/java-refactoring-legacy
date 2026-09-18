import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';

export class ArtifactPublisher {
  publishSnapshot(artifactName: string, buildNumber: number): string {
    return this.publish(artifactName, buildNumber, '-SNAPSHOT');
  }

  publishRelease(artifactName: string, buildNumber: number): string {
    return this.publish(artifactName, buildNumber, '');
  }

  private publish(
    artifactName: string,
    buildNumber: number,
    qualifier: string,
  ): string {
    const normalizedName = this.validateAndNormalize(
      artifactName,
      buildNumber);
    return normalizedName + ':' + buildNumber + qualifier;
  }

  private validateAndNormalize(
    artifactName: string,
    buildNumber: number,
  ): string {
    requireNonNull(artifactName, 'artifactName');
    if (artifactName.trim() === '') {
      throw new IllegalArgumentError('artifactName must not be blank');
    }
    if (buildNumber <= 0) {
      throw new IllegalArgumentError(
        'buildNumber must be greater than zero');
    }

    // toLowerCase() jest niezależne od locale (jak toLowerCase(Locale.ROOT));
    // toLocaleLowerCase() zależałoby od ustawień regionalnych (np. tr-TR: I → ı).
    return artifactName.trim().toLowerCase();
  }
}
