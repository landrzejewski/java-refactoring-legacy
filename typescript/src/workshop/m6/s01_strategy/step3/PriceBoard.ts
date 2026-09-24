import type { Money } from '../../../shared/Money.js';
import type { PriceRequest } from '../PriceRequest.js';
import { DiscountPrograms } from './DiscountPrograms.js';
import { TicketPricer } from './TicketPricer.js';

/** Krok 3: klient składa kontekst ze strategią wybraną w DiscountPrograms (Change Signature). */
export class PriceBoard {
  priceFor(request: PriceRequest): Money {
    return new TicketPricer(DiscountPrograms.forName(request.program))
      .price(request.base, request.ticketType);
  }
}
