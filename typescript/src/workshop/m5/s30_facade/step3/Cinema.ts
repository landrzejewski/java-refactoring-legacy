import { BookingFacade } from './BookingFacade.js';
import { BoxOffice } from './BoxOffice.js';
import { OnlineShop } from './OnlineShop.js';

/**
 * Stabilny punkt wejścia sceny (korzeń kompozycji). Krok 3: podsystemy widzi już tylko fasada
 * (moduł BookingFacade) - tu dostajemy gotową fasadę i podajemy ją kanałom sprzedaży.
 */
export class Cinema {
  private readonly booking = BookingFacade.create();
  private readonly shop = new OnlineShop(this.booking);
  private readonly desk = new BoxOffice(this.booking);

  onlineShop(): OnlineShop {
    return this.shop;
  }

  boxOffice(): BoxOffice {
    return this.desk;
  }

  /** Obserwowalny stan wszystkich podsystemów naraz. */
  state(): string {
    return this.booking.state();
  }
}
