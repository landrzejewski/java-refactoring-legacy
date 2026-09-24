import { Money } from '../../../shared/Money.js';

/**
 * Krok 2: najpierw przenosimy zachowanie korzystające z pola. Baza liczy cenę z punktem
 * rozszerzenia `surcharge()`; o dopłacie VIP decyduje już StandardTicket.
 */
export abstract class Ticket {
  readonly #basePrice: Money;
  #vipUpgraded = false;

  protected constructor(basePrice: Money) {
    this.#basePrice = basePrice;
  }

  upgradeToVip(): void {
    this.#vipUpgraded = true;
  }

  isVipUpgraded(): boolean {
    return this.#vipUpgraded;
  }

  price(): Money {
    return this.#basePrice.minus(this.#basePrice.percent(this.discountPercent())).plus(this.surcharge());
  }

  protected surcharge(): Money {
    return Money.ZERO;
  }

  protected abstract discountPercent(): number;
}
