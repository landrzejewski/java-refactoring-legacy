import type { BiOperator } from './BiOperator.js';
import type { Expression } from './Expression.js';

export class Operation implements Expression {
  constructor(
    private readonly left: Expression,
    private readonly right: Expression,
    private readonly operator: BiOperator,
  ) {}

  evaluate(context: ReadonlyMap<string, number>): number {
    return this.operator.apply(this.left.evaluate(context), this.right.evaluate(context));
  }
}
