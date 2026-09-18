import type { IdGenerator } from './IdGenerator.js';

export class IncrementalIdGenerator implements IdGenerator {
  // Java: PATTERN = "%020d"
  private counter = 0;

  getNext(): string {
    return String(++this.counter).padStart(20, '0');
  }
}
