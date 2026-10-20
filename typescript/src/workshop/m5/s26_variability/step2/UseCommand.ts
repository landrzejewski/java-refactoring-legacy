import { Status } from '../Status.js';
import type { BookingLedger } from './BookingLedger.js';
import { type DeskCommand, idFrom } from './DeskCommand.js';

/** Krok 2: USE jako polecenie - cofnięcie przywraca tylko poprzedni status. */
export class UseCommand implements DeskCommand {
  private previous = Status.PAID;

  private constructor(private readonly id: string) {}

  static from(args: readonly string[]): UseCommand {
    return new UseCommand(idFrom(args, 'USE'));
  }

  execute(ledger: BookingLedger): string {
    this.previous = ledger.status(this.id);
    ledger.use(this.id);
    return `${this.id} USED`;
  }

  undo(ledger: BookingLedger): void {
    ledger.restore(this.id, this.previous);
  }

  journalEntry(): string {
    return `USE ${this.id}`;
  }
}
