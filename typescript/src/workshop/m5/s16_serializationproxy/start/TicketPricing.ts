import type { Money } from '../../../shared/Money.js';

/**
 * Start: serwis z polem prywatnym ES (#) i bez interfejsu. Pole # czyni klasę nominalną: jako
 * TicketPricing przejdzie tylko prawdziwa instancja (albo podklasa), więc opakowania audytowego
 * nie da się podstawić. Przezroczysty Proxy JS też nie pomoże - metoda wołana przez proxy ma
 * this = proxy, a proxy nie ma pola # (TypeError). Audyt, cache czy transakcje "z zewnątrz" nie zadziałają.
 */
export class TicketPricing {
  readonly #studentDiscountPercent = 25;

  studentPrice(basePrice: Money): Money {
    return basePrice.minus(basePrice.percent(this.#studentDiscountPercent));
  }
}
