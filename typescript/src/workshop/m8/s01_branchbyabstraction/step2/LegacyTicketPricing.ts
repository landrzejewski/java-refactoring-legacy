import { Decimal } from 'decimal.js';

import { Money } from '../../../shared/Money.js';
import type { BookingRequest } from '../BookingRequest.js';
import type { TicketPricing } from './TicketPricing.js';

/**
 * Krok 2: stary cennik za abstrakcją TicketPricing. Wnętrze bez zmian,
 * jedynie na granicy zamieniamy double na Money (adapter do nowego kontraktu).
 */
export class LegacyTicketPricing implements TicketPricing {
  total(request: BookingRequest): Money {
    const s = request.screening;
    let sum = 0;
    for (let i = 0; i < request.seats.length; i++) {
      let p = 0;
      const f = s.format;
      if (f === 1) {
        p = 25.00;
      } else if (f === 2) {
        p = 32.00;
      } else if (f === 3) {
        p = 40.00;
      }
      const type = request.types[i]!;
      if (type === 'S') {
        p = p - p * 0.25;
      } else if (type === 'E') {
        p = p - p * 0.30;
      } else if (type === 'C') {
        p = p - p * 0.40;
      }
      if (s.start.hour < 12) {
        p = p - 5;
      }
      if (Number.parseInt(request.seats[i]!.substring(1), 10) >= s.vipFromRow) {
        p = p + 10;
      }
      if (f === 2 && !request.ownGlasses) {
        p = p + 3;
      }
      sum = sum + p;
    }
    if (request.seats.length >= 10) {
      sum = sum - sum * 0.10;
    }
    sum = Math.round(sum * 100) / 100.0;
    let total = sum;
    if (request.web) {
      total = total + 2.00 * request.seats.length;
    }
    return new Money(new Decimal(total));
  }
}
