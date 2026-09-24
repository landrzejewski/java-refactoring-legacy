import { Money } from '../../../shared/Money.js';

/**
 * Krok 3 (rozwiązanie): Push Members Down - upgradeToVip(), isVipUpgraded() i pole #vipUpgraded
 * trafiły do StandardTicket. Baza obiecuje tylko to, co prawdziwe dla wszystkich biletów.
 */
export abstract class Ticket {
  readonly #basePrice: Money;

  protected constructor(basePrice: Money) {
    this.#basePrice = basePrice;
  }

  price(): Money {
    return this.#basePrice.minus(this.#basePrice.percent(this.discountPercent())).plus(this.surcharge());
  }

  protected surcharge(): Money {
    return Money.ZERO;
  }

  protected abstract discountPercent(): number;
}
