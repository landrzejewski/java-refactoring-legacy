import { JulLogger } from '../../JulLogger.js';
import type { ConnectionUrlBuilder } from './ConnectionUrlBuilder.js';

export class Director {
  private static readonly log = JulLogger.getLogger('pl.training.patterns.creational.builder.Director');

  constructor(private readonly connectionUrlBuilder: ConnectionUrlBuilder) {}

  run(): void {
    const connectionUrl = this.connectionUrlBuilder.build();
    Director.log.info(`Connection url: ${connectionUrl}`, 'run');
  }
}
