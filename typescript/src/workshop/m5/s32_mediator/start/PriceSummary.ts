import { Money } from '../../../shared/Money.js';
import type { Basket } from './Basket.js';
import type { PayButton } from './PayButton.js';

/**
 * Start: podsumowanie ceny - rząd C (VIP) 35.00, pozostałe 25.00, voucher KINO10 to -10%.
 * Po wpisaniu vouchera samo pyta koszyk o miejsca i samo odświeża przycisk.
 */
export class PriceSummary {
  private discountPercent = 0;
  private amount = Money.ZERO;
  // uzupełniane przez connect() - wiązanie "każdy z każdym" po utworzeniu komponentów
  private basket!: Basket;
  private payButton!: PayButton;

  connect(basket: Basket, payButton: PayButton): void {
    this.basket = basket;
    this.payButton = payButton;
  }

  /** Akcja użytkownika: voucher; nieznany kod usuwa zniżkę. */
  applyVoucher(code: string): void {
    this.discountPercent = code === 'KINO10' ? 10 : 0;
    this.recalculate(this.basket.seats());
    this.payButton.refresh(this.basket.seats().length, this.amount);
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
