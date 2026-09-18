import { randomUUID } from 'node:crypto';
import type { IdGenerator } from './IdGenerator.js';

export class UuidGenerator implements IdGenerator {
  getNext(): string {
    return randomUUID();
  }
}
