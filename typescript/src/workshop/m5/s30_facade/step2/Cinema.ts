import { BookingFacade } from './BookingFacade.js';
import { BoxOffice } from './BoxOffice.js';
import { OnlineShop } from './OnlineShop.js';
import { PaymentClient } from './PaymentClient.js';
import { PriceList } from './PriceList.js';
import { SeatRegistry } from './SeatRegistry.js';
import { TicketMailer } from './TicketMailer.js';

// Jak List.toString() w Javie: [a, b].
const list = (items: readonly string[]): string => `[${items.join(', ')}]`;

/** Stabilny punkt wejścia sceny (korzeń kompozycji). Krok 2: oba kanały dostają tylko fasadę. */
export class Cinema {
  private readonly seats = new SeatRegistry();
  private readonly prices = new PriceList();
  private readonly payments = new PaymentClient();
  private readonly mailer = new TicketMailer();
  private readonly booking = new BookingFacade(this.seats, this.prices, this.payments, this.mailer);
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
    return `miejsca=${this.seats.describe()} platnosci=${list(this.payments.log())} maile=${list(this.mailer.sent())}`;
  }
}
