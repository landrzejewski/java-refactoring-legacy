import { Decimal } from 'decimal.js';

import type { BookingRequest } from '../BookingRequest.js';
import { Pricing } from './Pricing.js';

/** Krok 2: Extract Class - cennik ma jednego właściciela i zwraca wynik zamiast dwóch zmiennych. */
export class TicketPricer {
  price(request: BookingRequest): Pricing {
    let base: Decimal;
    switch (request.format) {
      case 'IMAX': base = new Decimal('40.00'); break;
      case '3D': base = new Decimal('32.00'); break;
      default: base = new Decimal('25.00');
    }
    let total = new Decimal(0);
    let vipSeats = 0;
    for (const seat of request.seats) {
      total = total.plus(base);
      if (Number.parseInt(seat.substring(1), 10) >= 10) {
        total = total.plus(new Decimal('10.00'));
        vipSeats++;
      }
    }
    return new Pricing(total, vipSeats);
  }
}
