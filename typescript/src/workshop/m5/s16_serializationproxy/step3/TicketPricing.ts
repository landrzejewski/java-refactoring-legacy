import type { Money } from '../../../shared/Money.js';
import type { Pricing } from './Pricing.js';

/**
 * Krok 3 (rozwiązanie): implementacja może zachować pole # - klienci zależą od roli Pricing, a proxy
 * audytowe woła metody na prawdziwym obiekcie. Uwaga: wywołanie this.innaMetoda() wewnątrz klasy
 * i tak omija proxy (self-invocation).
 */
export class TicketPricing implements Pricing {
  readonly #studentDiscountPercent = 25;

  studentPrice(basePrice: Money): Money {
    return basePrice.minus(basePrice.percent(this.#studentDiscountPercent));
  }
}
