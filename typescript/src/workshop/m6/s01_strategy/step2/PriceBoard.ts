import type { Money } from '../../../shared/Money.js';
import type { PriceRequest } from '../PriceRequest.js';
import { TicketPricer } from './TicketPricer.js';

/** Krok 2: klient bez zmian. */
export class PriceBoard {
  private readonly pricer = new TicketPricer();

  priceFor(request: PriceRequest): Money {
    return this.pricer.price(request.base, request.ticketType, request.program);
  }
}
