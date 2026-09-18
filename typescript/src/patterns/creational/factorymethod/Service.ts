import { JulLogger } from '../../JulLogger.js';
import type { IdGeneratorFactory } from './IdGeneratorFactory.js';

export class Service {
  private static readonly log = JulLogger.getLogger('pl.training.patterns.creational.factorymethod.Service');

  constructor(private readonly idGeneratorFactory: IdGeneratorFactory) {}

  run(): void {
    const idGenerator = this.idGeneratorFactory.create();
    Service.log.info(`Id: ${idGenerator.getNext()}`, 'run');
  }
}
