import type { IdGenerator } from './IdGenerator.js';

export interface IdGeneratorFactory {
  create(): IdGenerator;
}
