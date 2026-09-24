import type { ConsoleCommand } from './ConsoleCommand.js';
import type { Till } from './Till.js';

/** Krok 3: gałąź REPORT jako obiekt komendy. */
export class ReportCommand implements ConsoleCommand {
  execute(_args: string, till: Till): string {
    return `Kasa: ${till.cash().toString()}, biletow: ${till.tickets()}`;
  }
}
