import { Money } from '../../../shared/Money.js';
import { Status } from '../Status.js';
import type { BookingLedger } from './BookingLedger.js';
import { type DeskCommand, idFrom } from './DeskCommand.js';

/**
 * Krok 2: CANCEL jako polecenie. Ile oddać, decyduje odbiorca (to zależy od statusu);
 * polecenie tylko zapamiętuje wynik, żeby cofnięcie wpłaciło go z powrotem.
 */
export class CancelCommand implements DeskCommand {
  private previous = Status.NEW;
  private refunded = Money.ZERO;

  private constructor(private readonly id: string) {}

  static from(args: readonly string[]): CancelCommand {
    return new CancelCommand(idFrom(args, 'CANCEL'));
  }

  execute(ledger: BookingLedger): string {
    this.previous = ledger.status(this.id);
    this.refunded = ledger.cancel(this.id);
    return `${this.id} CANCELLED${this.refunded.isGreaterThan(Money.ZERO) ? ` zwrot ${this.refunded.toString()}` : ''}`;
  }

  undo(ledger: BookingLedger): void {
    ledger.deposit(this.refunded);
    ledger.restore(this.id, this.previous);
  }

  journalEntry(): string {
    return `CANCEL ${this.id}`;
  }
}
