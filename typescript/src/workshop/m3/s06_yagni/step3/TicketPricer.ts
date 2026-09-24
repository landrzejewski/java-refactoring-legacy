import { Decimal } from 'decimal.js';

import type { TicketQuote } from '../TicketQuote.js';

/**
 * Krok 3 (rozwiązanie): Inline Class dla obu reguł i Safe Delete interfejsu
 * PricingRule. Dwie aktualne reguły to dwa nazwane warunki - bez silnika.
 *
 * Czego YAGNI NIE zabrania i co tu zostaje: testów każdej reguły, nazwanych
 * stałych i metod (isMorning, isVip), szwu testowego (cena liczona z danych wejściowych,
 * bez zegara i statycznego stanu). Gdy pojawi się trzecia reguła z innym właścicielem
 * lub konfiguracja od biznesu, wydzielimy abstrakcję wtedy - w małych krokach, pod testami.
 */
export class TicketPricer {
  private static readonly MORNING_DISCOUNT = new Decimal('5.00');
  private static readonly VIP_SURCHARGE = new Decimal('10.00');

  price(quote: TicketQuote): Decimal {
    let price = this.basePrice(quote.format);
    if (this.isMorning(quote)) {
      price = price.minus(TicketPricer.MORNING_DISCOUNT);
    }
    if (this.isVip(quote)) {
      price = price.plus(TicketPricer.VIP_SURCHARGE);
    }
    return price;
  }

  private isMorning(quote: TicketQuote): boolean {
    return quote.start.hour < 12;
  }

  private isVip(quote: TicketQuote): boolean {
    return quote.row >= quote.vipFromRow;
  }

  private basePrice(format: string): Decimal {
    switch (format) {
      case 'IMAX': return new Decimal('40.00');
      case '3D': return new Decimal('32.00');
      default: return new Decimal('25.00');
    }
  }
}
