import { requireNonNull } from '../../shared/requireNonNull.js';
import type { ReadinessProblem } from './ReadinessProblem.js';

export class ReviewReadiness {
  readonly problems: readonly ReadinessProblem[];

  constructor(problems: readonly ReadinessProblem[]) {
    this.problems = Object.freeze([...requireNonNull(problems, 'problems')]);
  }

  ready(): boolean {
    return this.problems.length === 0;
  }
}
