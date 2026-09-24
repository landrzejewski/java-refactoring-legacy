import { Money } from '../../../shared/Money.js';
import { PriceList } from './PriceList.js';

/** Krok 1: klient pyta o jedyną instancję zamiast robić new. */
export class TicketDesk {
  quote(format: string, online: boolean): Money {
    const price = PriceList.getInstance().basePrice(format);
    return online ? price.plus(Money.of('2.00')) : price;
  }
}
