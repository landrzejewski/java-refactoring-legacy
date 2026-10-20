import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { Expression } from './Expression.js';

export class Variable implements Expression {
  constructor(private readonly name: string) {}

  evaluate(context: ReadonlyMap<string, number>): number {
    // Java: unboxing context.get(name) throws NullPointerException for an unknown variable
    return requireNonNull(context.get(this.name));
  }
}
