import type { IdGenerator } from './IdGenerator.js';
import type { IdGeneratorFactory } from './IdGeneratorFactory.js';
import { IncrementalIdGenerator } from './IncrementalIdGenerator.js';

export class TestIdGeneratorFactory implements IdGeneratorFactory {
  create(): IdGenerator {
    return new IncrementalIdGenerator();
  }
}
