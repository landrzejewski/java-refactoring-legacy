import { Money } from '../../../shared/Money.js';
import type { TicketQuery } from '../TicketQuery.js';

/**
 * Krok 3 (commit 3, zmiana zachowania): nowa reguła "Tani wtorek" - NORMAL -20% ceny bazowej
 * we wtorek. Diff to kilka linii, więc recenzent widzi wyłącznie nową regułę biznesową.
 */
export class PriceList {
  private static readonly CHEAP_TUESDAY_PERCENT = 20;

  price(query: TicketQuery): Money {
    const base = PriceList.basePrice(query.format);
    let price = base.minus(base.percent(PriceList.discountPercent(query)));
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

  private static discountPercent(query: TicketQuery): number {
    switch (query.type) {
      case 'STUDENT': return 25;
      case 'SENIOR': return 30;
      case 'CHILD': return 40;
      default: return PriceList.isCheapTuesday(query) ? PriceList.CHEAP_TUESDAY_PERCENT : 0;
    }
  }

  private static isCheapTuesday(query: TicketQuery): boolean {
    return query.start.dayOfWeek === 'TUESDAY';
  }
}
