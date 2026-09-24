import { Money } from '../../../shared/Money.js';
import type { LocalDateTime } from '../../../shared/time.js';
import type { Booking } from '../Booking.js';

const CANCELLATION_FEE = Money.of('3.00');

/**
 * Start: kwota zwrotu. Pierwszy if pyta o implementację (status, czas, prefiks kodu promocji),
 * a nie o regułę "czy rezerwacji przysługuje zwrot". Drugi if ukrywa próg 24 godzin.
 */
export class RefundCalculator {
  refund(b: Booking, now: LocalDateTime): Money {
    let amount = Money.ZERO;
    if (b.status === 'PAID' && now.isBefore(b.screeningStart)
      && !(b.promo !== null && b.promo.startsWith('FREE'))) {
      if (!now.plusHours(24).isAfter(b.screeningStart)) {
        amount = b.tickets;
      } else {
        amount = b.tickets.percent(50);
      }
    }
    return amount.minus(CANCELLATION_FEE).max(Money.ZERO);
  }
}
