import type { BiOperator } from './BiOperator.js';

export class Minus implements BiOperator {
  apply(a: number, b: number): number {
    return a - b;
  }
}
