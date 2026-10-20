import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { BookingLedger } from './BookingLedger.js';
import type { DeskCommand } from './DeskCommand.js';
import { TicketPricings } from './TicketPricings.js';

/** Krok 3 (bez zmian od kroku 2): BOOK jako polecenie. Cenę liczą strategie z kroku 1, cofnięcie usuwa rezerwację. */
export class BookCommand implements DeskCommand {
  private static readonly FORMATS: ReadonlyMap<string, Money> = new Map([
    ['2D', Money.of('25.00')],
    ['3D', Money.of('32.00')],
    ['IMAX', Money.of('40.00')],
  ]);

  private readonly ticketTypes: readonly string[];

  private constructor(private readonly id: string, private readonly format: string, ticketTypes: readonly string[]) {
    this.ticketTypes = Object.freeze([...ticketTypes]);
  }

  static from(args: readonly string[]): BookCommand {
    if (args.length < 4) {
      throw new IllegalArgumentError('BOOK <id> <format> <typ>...');
    }
    return new BookCommand(args[1] ?? '', args[2] ?? '', args.slice(3));
  }

  execute(ledger: BookingLedger): string {
    const base = BookCommand.FORMATS.get(this.format);
    if (base === undefined) {
      throw new IllegalArgumentError(`unknown format: ${this.format}`);
    }
    let total = Money.ZERO;
    for (const type of this.ticketTypes) {
      total = total.plus(TicketPricings.forType(type).price(base));
    }
    ledger.open(this.id, total);
    return `${this.id} NEW ${total.toString()}`;
  }

  undo(ledger: BookingLedger): void {
    ledger.remove(this.id);
  }

  journalEntry(): string {
    return `BOOK ${this.id}`;
  }
}
