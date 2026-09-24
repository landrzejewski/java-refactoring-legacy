import { Money } from '../../../shared/Money.js';

/**
 * Krok 1: bez zmian - baza nadal zna upgradeToVip().
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
    const price = this.#basePrice.minus(this.#basePrice.percent(this.discountPercent()));
    return this.isVipUpgraded() ? price.plus(Money.of('10.00')) : price;
  }

  protected abstract discountPercent(): number;
}
