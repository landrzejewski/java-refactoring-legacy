import { JulLogger } from '../../JulLogger.js';
import type { Consumer } from './EventsBus.js';
import type { ServerEvent } from './ServerEvent.js';

export class Logger implements Consumer<ServerEvent> {
  private static readonly log = JulLogger.getLogger('pl.training.patterns.behavioral.observer.Logger');

  accept(serverEvent: ServerEvent): void {
    Logger.log.info(serverEvent.getPayload(), 'accept');
  }
}
