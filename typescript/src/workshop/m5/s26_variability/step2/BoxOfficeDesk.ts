import { IllegalArgumentError, IllegalStateError } from '../../../../shared/errors.js';
import { BookCommand } from './BookCommand.js';
import { BookingLedger } from './BookingLedger.js';
import { CancelCommand } from './CancelCommand.js';
import type { DeskCommand } from './DeskCommand.js';
import { PayCommand } from './PayCommand.js';
import { UseCommand } from './UseCommand.js';

type CommandFactory = (args: readonly string[]) => DeskCommand;

/**
 * Krok 2: oś "operacje kasjera" jako Command. Każda operacja to obiekt, który wykonuje się na
 * odbiorcy (BookingLedger), pamięta, co trzeba odwrócić, i sam opisuje się w dzienniku.
 * Historia poleceń zastępuje dziennik z rekordami i switch w undo(). Cykl życia rezerwacji
 * jeszcze siedzi w warunkach odbiorcy - to następny krok.
 */
export class BoxOfficeDesk {
  private static readonly COMMANDS: ReadonlyMap<string, CommandFactory> = new Map<string, CommandFactory>([
    ['BOOK', (args) => BookCommand.from(args)],
    ['PAY', (args) => PayCommand.from(args)],
    ['USE', (args) => UseCommand.from(args)],
    ['CANCEL', (args) => CancelCommand.from(args)],
  ]);

  private readonly ledger = new BookingLedger();
  private readonly history: DeskCommand[] = [];

  handle(line: string): string {
    const args = line.trim().split(/ +/);
    const operation = (args[0] ?? '').toUpperCase();
    try {
      switch (operation) {
        case 'UNDO':
          return this.undo();
        case 'REPORT':
          return this.report();
        default:
          return this.execute(operation, args);
      }
    } catch (exception) {
      if (exception instanceof IllegalArgumentError || exception instanceof IllegalStateError) {
        return `Blad: ${exception.message}`;
      }
      throw exception;
    }
  }

  private execute(operation: string, args: readonly string[]): string {
    const factory = BoxOfficeDesk.COMMANDS.get(operation);
    if (factory === undefined) {
      return `Nieznana operacja: ${args[0] ?? ''}`;
    }
    const command = factory(args);
    const result = command.execute(this.ledger);
    this.history.push(command);
    return result;
  }

  private undo(): string {
    const last = this.history.pop();
    if (last === undefined) {
      throw new IllegalStateError('nothing to undo');
    }
    last.undo(this.ledger);
    return `Cofnieto: ${last.journalEntry()}`;
  }

  private report(): string {
    const log = this.history.length === 0 ? '-' : this.history
      .map((command) => command.journalEntry())
      .join(', ');
    return `${this.ledger.summary()}; dziennik: ${log}`;
  }
}
