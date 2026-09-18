import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import type { RiskLevel } from './RiskLevel.js';

export class RiskAssessment {
  constructor(
    readonly score: number,
    readonly level: RiskLevel,
  ) {
    if (score < 0 || score > 100) {
      throw new IllegalArgumentError(
        'score must be between 0 and 100');
    }
    requireNonNull(level, 'level');
  }
}
