import { javaLocalDateTime } from '../../JavaText.js';
import { JulLogger } from '../../JulLogger.js';
import type { Command } from './Command.js';

export class PrintTime implements Command {
  private static readonly log = JulLogger.getLogger('pl.training.patterns.behavioral.command.PrintTime');

  execute(): void {
    PrintTime.log.info(javaLocalDateTime(new Date()), 'execute'); // Java: LocalDateTime.now().toString()
  }
}
