import type { BiOperator } from './BiOperator.js';

export class Multiply implements BiOperator {
  apply(a: number, b: number): number {
    return a * b;
  }
}
