import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import { unmodifiableSet } from '../UnmodifiableSet.js';
import type { ChangeIntent } from './ChangeIntent.js';
import type { VerificationEvidence } from './VerificationEvidence.js';

export class ChangeSet {
  readonly title: string;
  readonly intents: ReadonlySet<ChangeIntent>;
  readonly verificationEvidence: readonly VerificationEvidence[];
  readonly independentlyGreenBuild: boolean;

  constructor(
    title: string,
    intents: Iterable<ChangeIntent>,
    verificationEvidence: readonly VerificationEvidence[],
    independentlyGreenBuild: boolean,
  ) {
    requireNonNull(title, 'title');
    if (title.trim().length === 0) {
      throw new IllegalArgumentError('title must not be blank');
    }
    this.title = title;
    // Kopie chronią przed późniejszą modyfikacją kolekcji wejściowych.
    this.intents = unmodifiableSet(requireNonNull(intents, 'intents'));
    this.verificationEvidence = Object.freeze([
      ...requireNonNull(verificationEvidence, 'verificationEvidence'),
    ]);
    this.independentlyGreenBuild = independentlyGreenBuild;
  }
}
