import type { IdGenerator } from './IdGenerator.js';
import type { IdGeneratorFactory } from './IdGeneratorFactory.js';
import { UuidGenerator } from './UuidGenerator.js';

export class ProductionIdGeneratorFactory implements IdGeneratorFactory {
  private readonly uuidGenerator = new UuidGenerator();

  create(): IdGenerator {
    return this.uuidGenerator;
  }
}
