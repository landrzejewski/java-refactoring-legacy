import { Decimal } from 'decimal.js';

/**
 * Krok 2 (rozwiązanie): Inline Variable dla stałych flag i Simplify - martwe gałęzie
 * karnetu znikają. Zostaje czysta reguła biletu: format, poranek, okulary 3D.
 */
export class TicketCounter {
  private static readonly MORNING_DISCOUNT = new Decimal('5.00');
  private static readonly GLASSES_3D = new Decimal('3.00');

  ticket(format: string, morning: boolean, ownGlasses: boolean): Decimal {
    let price: Decimal;
    switch (format) {
      case 'IMAX': price = new Decimal('40.00'); break;
      case '3D': price = new Decimal('32.00'); break;
      default: price = new Decimal('25.00');
    }
    if (morning) {
      price = price.minus(TicketCounter.MORNING_DISCOUNT);
    }
    if (format === '3D' && !ownGlasses) {
      price = price.plus(TicketCounter.GLASSES_3D);
    }
    return price;
  }
}
