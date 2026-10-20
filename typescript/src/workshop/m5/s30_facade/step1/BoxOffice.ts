import { IllegalStateError } from '../../../../shared/errors.js';
import type { TicketOrder } from '../TicketOrder.js';
import type { PaymentClient } from './PaymentClient.js';
import type { PriceList } from './PriceList.js';
import type { SeatRegistry } from './SeatRegistry.js';
import type { TicketMailer } from './TicketMailer.js';

/**
 * Krok 1: bez zmian - kasa nadal orkiestruje podsystemy sama. Zestawienie jej metody
 * z BookingFacade.book pokazuje różnicę: przy odmowie płatności kasa nie zwalnia miejsc.
 */
export class BoxOffice {
  constructor(
    private readonly seats: SeatRegistry,
    private readonly prices: PriceList,
    private readonly payments: PaymentClient,
    private readonly mailer: TicketMailer,
  ) {}

  sell(order: TicketOrder): string {
    const total = this.prices.price(order.ticketType).times(order.seats.length);
    try {
      this.seats.reserve(order.screening, order.seats);
    } catch (exception) {
      if (exception instanceof IllegalStateError) {
        return `ERROR ${exception.message}`;
      }
      throw exception;
    }
    if (!this.payments.charge(order.card, total)) {
      return 'ERROR payment declined';
    }
    this.mailer.send(order.email, `Bilety ${order.screening} ${order.seats.join(',')} = ${total.toString()}`);
    return `OK ${total.toString()}`;
  }
}
