import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';

/** Podsystem: cennik biletu 2D wg typu (N, S, E, C). */
export class PriceList {
  private static readonly PRICES: ReadonlyMap<string, Money> = new Map([
    ['N', Money.of('25.00')], ['S', Money.of('18.75')], ['E', Money.of('17.50')], ['C', Money.of('15.00')],
  ]);

  price(ticketType: string): Money {
    const price = PriceList.PRICES.get(ticketType);
    if (price === undefined) {
      throw new IllegalArgumentError(`unknown ticket type: ${ticketType}`);
    }
    return price;
  }
}
