import type { Expression } from './Expression.js';

export class Literal implements Expression {
  constructor(private readonly value: number) {}

  evaluate(_context: ReadonlyMap<string, number>): number {
    return this.value;
  }
}
