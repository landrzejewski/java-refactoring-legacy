import { JulLogger } from '../../JulLogger.js';
import { Handler } from './Handler.js';

export class Processor extends Handler {
  private static readonly log = JulLogger.getLogger('pl.training.patterns.behavioral.chainofresponsibility.Processor');

  handleRequest(request: string): void {
    Processor.log.info(`Processing: ${request}`, 'handleRequest');
  }
}
