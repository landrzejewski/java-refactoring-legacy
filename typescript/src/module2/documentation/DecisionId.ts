import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';

export class DecisionId {
  private static readonly FORMAT = /^ADR-[0-9]{4}$/;

  readonly value: string;

  constructor(value: string) {
    requireNonNull(value, 'value');
    if (!DecisionId.FORMAT.test(value)) {
      throw new IllegalArgumentError('value must use the format ADR-NNNN');
    }
    this.value = value;
  }

  toString(): string {
    return this.value;
  }
}
