import { requireNonNull } from '../../../shared/requireNonNull.js';
import { JulLogger } from '../../JulLogger.js';
import { Handler } from './Handler.js';

export class Logger extends Handler {
  private static readonly log = JulLogger.getLogger('pl.training.patterns.behavioral.chainofresponsibility.Logger');

  constructor(nextHandler: Handler) {
    super();
    this.nextHandler = nextHandler;
  }

  handleRequest(request: string): void {
    Logger.log.info(request, 'handleRequest');
    requireNonNull(this.nextHandler).handleRequest(request);
  }
}
