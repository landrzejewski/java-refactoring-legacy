import type { ConsoleCommand } from './ConsoleCommand.js';
import { RefundCommand } from './RefundCommand.js';
import { ReportCommand } from './ReportCommand.js';
import { SellCommand } from './SellCommand.js';
import { Till } from './Till.js';

/**
 * Krok 3: Replace Conditional Dispatcher with Command - rejestr komend zamiast if.
 * Równoważne, bo klucze są rozłączne; normalizacja klucza (toUpperCase) zachowana.
 */
export class CashierConsole {
  private readonly till = new Till();
  private readonly commands: ReadonlyMap<string, ConsoleCommand>;

  constructor(commands: ReadonlyMap<string, ConsoleCommand> = new Map<string, ConsoleCommand>([
    ['SELL', new SellCommand()],
    ['REFUND', new RefundCommand()],
    ['REPORT', new ReportCommand()],
  ])) {
    this.commands = new Map(commands);
  }

  handle(line: string): string {
    const [name = '', ...rest] = line.trim().split(' ');
    const command = this.commands.get(name.toUpperCase());
    if (command === undefined) {
      return `Nieznana komenda: ${name}`;
    }
    return command.execute(rest.join(' '), this.till);
  }
}
