import { IllegalArgumentError } from '../../../shared/errors.js';
import { isBlank } from '../../support.js';

export class ReleasePublished {
  constructor(readonly releaseId: string) {
    if (isBlank(releaseId)) {
      throw new IllegalArgumentError('releaseId must not be blank');
    }
  }
}
