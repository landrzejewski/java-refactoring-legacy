import { IllegalArgumentError } from '../../../shared/errors.js';
import { PublishedRelease } from '../PublishedRelease.js';

export class ReleaseValidator {
  validate(
    releaseId: string,
    service: string,
    version: string,
  ): PublishedRelease {
    ReleaseValidator.requireText(releaseId, 'releaseId');
    ReleaseValidator.requireText(service, 'service');
    ReleaseValidator.requireText(version, 'version');
    return new PublishedRelease(releaseId, service, version);
  }

  private static requireText(value: string | null, field: string): void {
    if (value == null || value.trim() === '') {
      throw new IllegalArgumentError(field + ' must not be blank');
    }
  }
}
