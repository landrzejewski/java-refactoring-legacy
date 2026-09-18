import { IllegalArgumentError, IllegalStateError } from '../../../shared/errors.js';

export class Contracts {
  private constructor() {
  }

  static require(condition: boolean, message: string): void {
    if (!condition) {
      throw new IllegalArgumentError(message);
    }
  }

  static ensure(condition: boolean, message: string): void {
    if (!condition) {
      throw new IllegalStateError(message);
    }
  }

  static invariant(condition: boolean, message: string): void {
    if (!condition) {
      throw new IllegalStateError(message);
    }
  }
}
