import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { BookingRequest } from '../BookingRequest.js';
import type { TicketPricing } from './TicketPricing.js';

/**
 * Krok 4: jedyna implementacja cennika - Money, nazwane reguły, jawne kody.
 * Stara implementacja zniknęła razem z przełącznikiem.
 */
export class ModernTicketPricing implements TicketPricing {
  private static readonly MORNING_DISCOUNT = Money.of('5.00');
  private static readonly VIP_SURCHARGE = Money.of('10.00');
  private static readonly GLASSES_3D = Money.of('3.00');
  private static readonly ONLINE_FEE = Money.of('2.00');
  private static readonly GROUP_SIZE = 10;

  total(request: BookingRequest): Money {
    let tickets = Money.ZERO;
    for (let i = 0; i < request.seats.length; i++) {
      tickets = tickets.plus(this.ticket(request, request.seats[i]!, request.types[i]!));
    }
    const count = request.seats.length;
    if (count >= ModernTicketPricing.GROUP_SIZE) {
      tickets = tickets.minus(tickets.percent(10));
    }
    const fees = request.web ? ModernTicketPricing.ONLINE_FEE.times(count) : Money.ZERO;
    return tickets.plus(fees);
  }

  private ticket(request: BookingRequest, seat: string, type: string): Money {
    const screening = request.screening;
    const base = ModernTicketPricing.basePrice(screening.format);
    let price = base.minus(base.percent(ModernTicketPricing.discountPercent(type)));
    if (screening.start.hour < 12) {
      price = price.minus(ModernTicketPricing.MORNING_DISCOUNT);
    }
    if (Number.parseInt(seat.substring(1), 10) >= screening.vipFromRow) {
      price = price.plus(ModernTicketPricing.VIP_SURCHARGE);
    }
    if (screening.format === 2 && !request.ownGlasses) {
      price = price.plus(ModernTicketPricing.GLASSES_3D);
    }
    return price;
  }

  private static basePrice(format: number): Money {
    switch (format) {
      case 1: return Money.of('25.00');
      case 2: return Money.of('32.00');
      case 3: return Money.of('40.00');
      default: throw new IllegalArgumentError('Nieznany format: ' + format);
    }
  }

  private static discountPercent(type: string): number {
    switch (type) {
      case 'S': return 25;
      case 'E': return 30;
      case 'C': return 40;
      default: return 0;
    }
  }
}
