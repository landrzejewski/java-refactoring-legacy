import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { TicketQuery } from '../TicketQuery.js';

/**
 * Start: nowy kalkulator (Money) napisany obok starego. Nikt jeszcze nie sprawdził,
 * czy na ruchu produkcyjnym liczy tak samo - dlatego nie wolno go po prostu włączyć.
 */
export class CandidatePriceCalculator {
  private static readonly MORNING_DISCOUNT = Money.of('5.00');
  private static readonly VIP_SURCHARGE = Money.of('10.00');
  private static readonly GLASSES_3D = Money.of('3.00');

  price(query: TicketQuery): Money {
    let base = CandidatePriceCalculator.basePrice(query.format);
    if (query.start.hour < 12) {
      base = base.minus(CandidatePriceCalculator.MORNING_DISCOUNT);
    }
    let price = base.minus(base.percent(CandidatePriceCalculator.discountPercent(query.type)));
    if (query.row >= 10) {
      price = price.plus(CandidatePriceCalculator.VIP_SURCHARGE);
    }
    if (query.format === '3D') {
      price = price.plus(CandidatePriceCalculator.GLASSES_3D);
    }
    return price;
  }

  private static basePrice(format: string): Money {
    switch (format) {
      case '2D': return Money.of('25.00');
      case '3D': return Money.of('32.00');
      case 'IMAX': return Money.of('40.00');
      default: throw new IllegalArgumentError('Nieznany format: ' + format);
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
