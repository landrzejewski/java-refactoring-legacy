import { IllegalStateError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { TicketOrder } from '../TicketOrder.js';
import type { PaymentClient } from './PaymentClient.js';
import type { PriceList } from './PriceList.js';
import type { SeatRegistry } from './SeatRegistry.js';
import type { TicketMailer } from './TicketMailer.js';

/**
 * Start: sklep internetowy sam orkiestruje cztery podsystemy w ustalonej kolejności:
 * cena, rezerwacja miejsc, płatność (odmowa zwalnia miejsca), mail z biletem.
 * Do tego opłata rezerwacyjna 2.00 za miejsce.
 */
export class OnlineShop {
  private static readonly BOOKING_FEE = Money.of('2.00');

  constructor(
    private readonly seats: SeatRegistry,
    private readonly prices: PriceList,
    private readonly payments: PaymentClient,
    private readonly mailer: TicketMailer,
  ) {}

  buy(order: TicketOrder): string {
    const total = this.prices.price(order.ticketType).plus(OnlineShop.BOOKING_FEE).times(order.seats.length);
    try {
      this.seats.reserve(order.screening, order.seats);
    } catch (exception) {
      if (exception instanceof IllegalStateError) {
        return `ERROR ${exception.message}`;
      }
      throw exception;
    }
    if (!this.payments.charge(order.card, total)) {
      this.seats.release(order.screening, order.seats);
      return 'ERROR payment declined';
    }
    this.mailer.send(order.email, `Bilety ${order.screening} ${order.seats.join(',')} = ${total.toString()}`);
    return `OK ${total.toString()}`;
  }
}
