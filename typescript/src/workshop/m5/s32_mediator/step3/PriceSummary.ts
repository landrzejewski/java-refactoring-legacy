import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { SelectionMediator } from './SelectionMediator.js';

/**
 * Krok 3: bez zmian od kroku 2 - podsumowanie zna tylko mediatora. Liczy cenę z miejsc, które
 * poda mu mediator (rząd C - VIP 35.00, pozostałe 25.00, voucher KINO10 to -10%).
 */
export class PriceSummary {
  private discountPercent = 0;
  private amount = Money.ZERO;
  private readonly mediator: SelectionMediator;

  constructor(mediator: SelectionMediator) {
    this.mediator = requireNonNull(mediator, 'mediator');
  }

  /** Akcja użytkownika: voucher; nieznany kod usuwa zniżkę. */
  applyVoucher(code: string): void {
    this.discountPercent = code === 'KINO10' ? 10 : 0;
    this.mediator.voucherApplied();
  }

  recalculate(seats: readonly string[]): void {
    let sum = Money.ZERO;
    for (const seat of seats) {
      sum = sum.plus(seat.startsWith('C') ? Money.of('35.00') : Money.of('25.00'));
    }
    this.amount = sum.minus(sum.percent(this.discountPercent));
  }

  total(): Money {
    return this.amount;
  }

  describe(): string {
    return `suma=${this.amount.toString()}`;
  }
}
