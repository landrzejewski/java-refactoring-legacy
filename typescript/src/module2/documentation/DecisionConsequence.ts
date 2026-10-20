import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import type { ConsequenceKind } from './ConsequenceKind.js';

export class DecisionConsequence {
  readonly kind: ConsequenceKind;
  readonly description: string;

  constructor(kind: ConsequenceKind, description: string) {
    this.kind = requireNonNull(kind, 'kind');
    this.description = requireNonNull(description, 'description');
    if (description.trim().length === 0) {
      throw new IllegalArgumentError('description must not be blank');
    }
  }
}
