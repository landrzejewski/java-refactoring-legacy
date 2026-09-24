import { Money } from '../../../shared/Money.js';

/** Krok 2: stan kasy wydzielony z konsoli - komendy dostają go jako argument i same są bezstanowe. */
export class Till {
  private static readonly PRICES: ReadonlyMap<string, Money> = new Map([
    ['Diuna', Money.of('40.00')],
    ['Kraina Lodu', Money.of('32.00')],
    ['Amator', Money.of('25.00')],
  ]);

  private cashAmount = Money.ZERO;
  private ticketCount = 0;

  priceOf(title: string): Money | undefined {
    return Till.PRICES.get(title);
  }

  sold(quantity: number, total: Money): void {
    this.cashAmount = this.cashAmount.plus(total);
    this.ticketCount += quantity;
  }

  refunded(price: Money): void {
    this.cashAmount = this.cashAmount.minus(price);
    this.ticketCount--;
  }

  cash(): Money {
    return this.cashAmount;
  }

  tickets(): number {
    return this.ticketCount;
  }
}
