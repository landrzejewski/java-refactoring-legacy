import type { Money } from '../../../shared/Money.js';
import type { PriceRequest } from '../PriceRequest.js';
import { TicketPricer } from './TicketPricer.js';

/** Start: klient cennika - kasa wycenia bilet wg programu z konfiguracji. */
export class PriceBoard {
  private readonly pricer = new TicketPricer();

  priceFor(request: PriceRequest): Money {
    return this.pricer.price(request.base, request.ticketType, request.program);
  }
}
