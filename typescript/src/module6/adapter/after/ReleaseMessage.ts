import { IllegalArgumentError } from '../../../shared/errors.js';
import { isBlank } from '../../support.js';

export class ReleaseMessage {
  constructor(
    readonly recipient: string,
    readonly releaseId: string,
  ) {
    if (isBlank(recipient)) {
      throw new IllegalArgumentError('recipient must not be blank');
    }
    if (isBlank(releaseId)) {
      throw new IllegalArgumentError('releaseId must not be blank');
    }
  }
}
