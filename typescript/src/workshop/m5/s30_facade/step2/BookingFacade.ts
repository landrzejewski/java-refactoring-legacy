import { IllegalStateError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';
import type { TicketOrder } from '../TicketOrder.js';
import type { PaymentClient } from './PaymentClient.js';
import type { PriceList } from './PriceList.js';
import type { SeatRegistry } from './SeatRegistry.js';
import type { TicketMailer } from './TicketMailer.js';

/**
 * Krok 2: fasada obsługuje oba kanały. Kasa dostała w prezencie kompensację, której
 * jej kopia nie miała - odmowa płatności w kasie zwalnia teraz miejsca. To zmiana zachowania
 * (naprawa błędu), udokumentowana osobnym testem, a nie "czysta" refaktoryzacja.
 */
export class BookingFacade {
  constructor(
    private readonly seats: SeatRegistry,
    private readonly prices: PriceList,
    private readonly payments: PaymentClient,
    private readonly mailer: TicketMailer,
  ) {}

  book(order: TicketOrder, feePerSeat: Money): string {
    const total = this.prices.price(order.ticketType).plus(feePerSeat).times(order.seats.length);
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
