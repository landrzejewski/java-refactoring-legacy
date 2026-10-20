import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import { isBlank } from '../support.js';

// Odpowiednik zagnieżdżonego enuma ChangeEntry.Kind.
export enum Kind {
  FEATURE = 'FEATURE',
  FIX = 'FIX',
  BREAKING = 'BREAKING',
}

export class ChangeEntry {
  constructor(
    readonly kind: Kind,
    readonly description: string,
  ) {
    requireNonNull(kind, 'kind');
    if (isBlank(description)) {
      throw new IllegalArgumentError('description must not be blank');
    }
  }
}
