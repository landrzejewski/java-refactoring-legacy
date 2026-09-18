import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';

export class DecisionOption {
  readonly name: string;
  readonly rationale: string;

  constructor(name: string, rationale: string) {
    this.name = DecisionOption.requireNonBlank(name, 'name');
    this.rationale = DecisionOption.requireNonBlank(rationale, 'rationale');
  }

  private static requireNonBlank(value: string, name: string): string {
    requireNonNull(value, name);
    if (value.trim().length === 0) {
      throw new IllegalArgumentError(`${name} must not be blank`);
    }
    return value;
  }
}
