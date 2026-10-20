import { Money } from '../../../shared/Money.js';
import { PriceList } from './PriceList.js';

/** Start: każda wycena tworzy i parsuje nowy cennik. */
export class TicketDesk {
  quote(format: string, online: boolean): Money {
    const price = new PriceList().basePrice(format);
    return online ? price.plus(Money.of('2.00')) : price;
  }
}
