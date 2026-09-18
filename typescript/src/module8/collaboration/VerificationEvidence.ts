import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import type { EvidenceKind } from './EvidenceKind.js';

export class VerificationEvidence {
  readonly kind: EvidenceKind;
  readonly observation: string;

  constructor(kind: EvidenceKind, observation: string) {
    this.kind = requireNonNull(kind, 'kind');
    this.observation = requireNonNull(observation, 'observation');
    if (observation.trim().length === 0) {
      throw new IllegalArgumentError('observation must not be blank');
    }
  }
}
