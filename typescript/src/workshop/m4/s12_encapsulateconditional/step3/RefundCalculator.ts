import { Money } from '../../../shared/Money.js';
import type { LocalDateTime } from '../../../shared/time.js';
import type { Booking } from '../Booking.js';

const CANCELLATION_FEE = Money.of('3.00');

/**
 * Krok 3 (rozwiązanie): Encapsulate Conditional dla gałęzi - `cancelledAtLeast24hBefore`.
 * Podwójne zaprzeczenie `!now.plusHours(24).isAfter(...)` zamknięte w nazwie z regulaminu.
 */
export class RefundCalculator {
  refund(b: Booking, now: LocalDateTime): Money {
    let amount = Money.ZERO;
    if (isRefundable(b, now)) {
      if (cancelledAtLeast24hBefore(b, now)) {
        amount = b.tickets;
      } else {
        amount = b.tickets.percent(50);
      }
    }
    return amount.minus(CANCELLATION_FEE).max(Money.ZERO);
  }
}

function isRefundable(b: Booking, now: LocalDateTime): boolean {
  return b.status === 'PAID' && now.isBefore(b.screeningStart) && !hasFreeTicketPromo(b);
}

function hasFreeTicketPromo(b: Booking): boolean {
  return b.promo !== null && b.promo.startsWith('FREE');
}

function cancelledAtLeast24hBefore(b: Booking, now: LocalDateTime): boolean {
  return !now.plusHours(24).isAfter(b.screeningStart);
}
