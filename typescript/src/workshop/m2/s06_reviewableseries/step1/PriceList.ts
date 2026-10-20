import { Money } from '../../../shared/Money.js';
import type { TicketQuery } from '../TicketQuery.js';

/**
 * Krok 1 (commit 1, refaktoryzacja): Extract Method basePrice i discountPercent, Money zamiast
 * Decimal. Zachowanie bez zmian - dowodem jest test równoważności, nie opis w commicie.
 */
export class PriceList {
  price(query: TicketQuery): Money {
    const base = PriceList.basePrice(query.format);
    let price = base.minus(base.percent(PriceList.discountPercent(query.type)));
    if (query.start.hour < 12) {
      price = price.minus(Money.of('5.00'));
    }
    if (query.row >= 10) {
      price = price.plus(Money.of('10.00'));
    }
    return price;
  }

  private static basePrice(format: string): Money {
    switch (format) {
      case 'IMAX': return Money.of('40.00');
      case '3D': return Money.of('32.00');
      default: return Money.of('25.00');
    }
  }

  private static discountPercent(type: string): number {
    switch (type) {
      case 'STUDENT': return 25;
      case 'SENIOR': return 30;
      case 'CHILD': return 40;
      default: return 0;
    }
  }
}
