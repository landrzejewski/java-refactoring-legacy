import { JulLogger } from '../../JulLogger.js';
import type { Command } from './Command.js';

export class ConnectToServer implements Command {
  private static readonly log = JulLogger.getLogger('pl.training.patterns.behavioral.command.ConnectToServer');

  execute(): void {
    ConnectToServer.log.info('Connecting...', 'execute');
    ConnectToServer.log.info('Connected...', 'execute');
  }
}
