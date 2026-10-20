import { Money } from '../../../shared/Money.js';
import { PriceList } from './PriceList.js';

/** Krok 2: klient używa stałej PriceList - wciąż ukryta, globalna zależność. */
export class TicketDesk {
  quote(format: string, online: boolean): Money {
    const price = PriceList.basePrice(format);
    return online ? price.plus(Money.of('2.00')) : price;
  }
}
