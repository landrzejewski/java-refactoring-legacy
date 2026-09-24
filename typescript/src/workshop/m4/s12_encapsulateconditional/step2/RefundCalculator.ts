import { Money } from '../../../shared/Money.js';
import type { LocalDateTime } from '../../../shared/time.js';
import type { Booking } from '../Booking.js';

const CANCELLATION_FEE = Money.of('3.00');

/**
 * Krok 2: Encapsulate Conditional - cały warunek jako `isRefundable(b, now)`.
 * if pyta teraz o regułę biznesową; szczegóły (status, czas, promocja) są w jednym miejscu.
 */
export class RefundCalculator {
  refund(b: Booking, now: LocalDateTime): Money {
    let amount = Money.ZERO;
    if (isRefundable(b, now)) {
      if (!now.plusHours(24).isAfter(b.screeningStart)) {
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
