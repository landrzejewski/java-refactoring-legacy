import { IllegalArgumentError } from '../../../shared/errors.js';
import { isBlank, splitInTwo } from '../../support.js';
import { ReleaseDraft } from './ReleaseDraft.js';

export class LegacyKeyValueReleaseImporter {
  importRelease(raw: string): ReleaseDraft {
    if (isBlank(raw)) {
      throw new IllegalArgumentError('input must not be blank');
    }

    let releaseId: string | undefined;
    let service: string | undefined;
    for (const field of raw.split(';')) {
      const pair = splitInTwo(field, '=');
      if (pair.length !== 2) {
        throw new IllegalArgumentError('expected key=value');
      }
      const [key, value] = pair as [string, string];
      switch (key.trim()) {
        case 'id':
          releaseId = value.trim();
          break;
        case 'service':
          service = value.trim();
          break;
        default:
          throw new IllegalArgumentError('unknown field: ' + key);
      }
    }

    if (releaseId === undefined || isBlank(releaseId) || service === undefined || isBlank(service)) {
      throw new IllegalArgumentError('releaseId and service are required');
    }
    return new ReleaseDraft(releaseId, service);
  }
}
