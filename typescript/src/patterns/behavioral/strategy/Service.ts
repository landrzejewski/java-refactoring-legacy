import { JulLogger } from '../../JulLogger.js';
import type { IdGenerator } from './IdGenerator.js';

export class Service {
  private static readonly log = JulLogger.getLogger('pl.training.patterns.behavioral.strategy.Service');

  constructor(private readonly idGenerator: IdGenerator) {}

  run(): void {
    Service.log.info(`Id: ${this.idGenerator.getNext()}`, 'run');
  }
}
