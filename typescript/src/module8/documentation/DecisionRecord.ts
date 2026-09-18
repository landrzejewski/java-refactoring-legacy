import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import type { DecisionConsequence } from './DecisionConsequence.js';
import type { DecisionId } from './DecisionId.js';
import type { DecisionOption } from './DecisionOption.js';
import type { DecisionStatus } from './DecisionStatus.js';

export class DecisionRecord {
  readonly id: DecisionId;
  readonly title: string;
  readonly status: DecisionStatus;
  readonly context: string;
  readonly decision: string;
  readonly consideredOptions: readonly DecisionOption[];
  readonly consequences: readonly DecisionConsequence[];
  readonly verificationMethod: string;

  constructor(
    id: DecisionId,
    title: string,
    status: DecisionStatus,
    context: string,
    decision: string,
    consideredOptions: readonly DecisionOption[],
    consequences: readonly DecisionConsequence[],
    verificationMethod: string,
  ) {
    this.id = requireNonNull(id, 'id');
    this.title = DecisionRecord.requireNonBlank(title, 'title');
    this.status = requireNonNull(status, 'status');
    this.context = DecisionRecord.requireNonBlank(context, 'context');
    this.decision = DecisionRecord.requireNonBlank(decision, 'decision');
    this.consideredOptions = DecisionRecord.nonEmptyCopy(consideredOptions, 'consideredOptions');
    this.consequences = DecisionRecord.nonEmptyCopy(consequences, 'consequences');
    this.verificationMethod = DecisionRecord.requireNonBlank(
      verificationMethod,
      'verificationMethod',
    );
  }

  private static requireNonBlank(value: string, name: string): string {
    requireNonNull(value, name);
    if (value.trim().length === 0) {
      throw new IllegalArgumentError(`${name} must not be blank`);
    }
    return value;
  }

  private static nonEmptyCopy<T>(values: readonly T[], name: string): readonly T[] {
    const copy = Object.freeze([...requireNonNull(values, name)]);
    if (copy.length === 0) {
      throw new IllegalArgumentError(`${name} must not be empty`);
    }
    return copy;
  }
}
