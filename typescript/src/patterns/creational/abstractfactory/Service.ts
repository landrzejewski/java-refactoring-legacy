import { JulLogger } from '../../JulLogger.js';
import type { ConnectionAbstractFactory } from './ConnectionAbstractFactory.js';

export class Service {
  private static readonly log = JulLogger.getLogger('pl.training.patterns.creational.abstractfactory.Service');

  constructor(private readonly connectionAbstractFactory: ConnectionAbstractFactory) {}

  run(): void {
    const connection = this.connectionAbstractFactory.createConnection();
    const securedConnection = this.connectionAbstractFactory.createSecuredConnection();
    Service.log.info(`Connection: ${connection.getPort()}`, 'run');
    Service.log.info(`Secured connection: ${securedConnection.getPort()}`, 'run');
  }
}
