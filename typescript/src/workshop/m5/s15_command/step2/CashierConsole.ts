import type { ConsoleCommand } from './ConsoleCommand.js';
import { RefundCommand } from './RefundCommand.js';
import { ReportCommand } from './ReportCommand.js';
import { SellCommand } from './SellCommand.js';
import { Till } from './Till.js';

/**
 * Krok 2: Extract Class dla każdej gałęzi - komendy jako obiekty, stan w Till.
 * Dyspozytor warunkowy jeszcze zostaje; zmieniamy jedną rzecz naraz.
 */
export class CashierConsole {
  private readonly till = new Till();
  private readonly sell: ConsoleCommand = new SellCommand();
  private readonly refund: ConsoleCommand = new RefundCommand();
  private readonly report: ConsoleCommand = new ReportCommand();

  handle(line: string): string {
    const [name = '', ...rest] = line.trim().split(' ');
    const command = name.toUpperCase();
    const args = rest.join(' ');
    if (command === 'SELL') {
      return this.sell.execute(args, this.till);
    } else if (command === 'REFUND') {
      return this.refund.execute(args, this.till);
    } else if (command === 'REPORT') {
      return this.report.execute(args, this.till);
    }
    return `Nieznana komenda: ${name}`;
  }
}
