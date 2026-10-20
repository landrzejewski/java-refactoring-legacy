import { Money } from '../../../shared/Money.js';
import { Status } from '../Status.js';
import type { BookingLedger } from './BookingLedger.js';
import { type DeskCommand, idFrom } from './DeskCommand.js';

/** Krok 3 (bez zmian od kroku 2): PAY jako polecenie - pamięta poprzedni status i pobraną kwotę, żeby je odwrócić. */
export class PayCommand implements DeskCommand {
  private previous = Status.NEW;
  private paid = Money.ZERO;

  private constructor(private readonly id: string) {}

  static from(args: readonly string[]): PayCommand {
    return new PayCommand(idFrom(args, 'PAY'));
  }

  execute(ledger: BookingLedger): string {
    this.previous = ledger.status(this.id);
    this.paid = ledger.pay(this.id);
    return `${this.id} PAID ${this.paid.toString()}`;
  }

  undo(ledger: BookingLedger): void {
    ledger.withdraw(this.paid);
    ledger.restore(this.id, this.previous);
  }

  journalEntry(): string {
    return `PAY ${this.id}`;
  }
}
