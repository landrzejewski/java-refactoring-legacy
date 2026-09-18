import { IllegalArgumentError } from '../../../shared/errors.js';
import { isBlank } from '../../support.js';
import { ReleaseDraft } from './ReleaseDraft.js';

export class LegacyPipeReleaseImporter {
  importRelease(raw: string): ReleaseDraft {
    if (isBlank(raw)) {
      throw new IllegalArgumentError('input must not be blank');
    }

    const fields = raw.split('|');
    if (fields.length !== 2) {
      throw new IllegalArgumentError('expected releaseId and service');
    }

    const releaseId = fields[0]!.trim();
    const service = fields[1]!.trim();
    if (isBlank(releaseId) || isBlank(service)) {
      throw new IllegalArgumentError('releaseId and service are required');
    }
    return new ReleaseDraft(releaseId, service);
  }
}
