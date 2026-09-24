import { Money } from '../../../shared/Money.js';
import type { LocalDateTime } from '../../../shared/time.js';
import type { Booking } from '../Booking.js';

const CANCELLATION_FEE = Money.of('3.00');

/**
 * Krok 1: Extract Method dla najmniejszego fragmentu - `hasFreeTicketPromo`.
 * Osłona `promo !== null &&` idzie RAZEM z testem prefiksu:
 * krótkie spięcie jest zachowaniem.
 */
export class RefundCalculator {
  refund(b: Booking, now: LocalDateTime): Money {
    let amount = Money.ZERO;
    if (b.status === 'PAID' && now.isBefore(b.screeningStart) && !hasFreeTicketPromo(b)) {
      if (!now.plusHours(24).isAfter(b.screeningStart)) {
        amount = b.tickets;
      } else {
        amount = b.tickets.percent(50);
      }
    }
    return amount.minus(CANCELLATION_FEE).max(Money.ZERO);
  }
}

function hasFreeTicketPromo(b: Booking): boolean {
  return b.promo !== null && b.promo.startsWith('FREE');
}
