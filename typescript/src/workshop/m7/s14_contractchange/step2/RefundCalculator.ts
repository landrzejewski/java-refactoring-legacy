import { Decimal } from 'decimal.js';

/**
 * Krok 2: "przy okazji" - w jednym ruchu number -> Decimal i krótsze obcięcie do zera.
 * Wygląda na porządki, ale zmienia DWIE rzeczy w kontrakcie: zaokrąglenie połówek
 * (29.175 -> 29.18 zamiast 29.17, bo Decimal liczy dokładnie) i format zera
 * (guard clause zwraca '0', a nie '0.00'). Test to wykrywa.
 */
export class RefundCalculator {
  private static readonly FEE = new Decimal('3.00');

  refund(ticketsPaid: number, minutesBeforeStart: number): string {
    const refund = new Decimal(ticketsPaid)
      .times(RefundCalculator.share(minutesBeforeStart))
      .minus(RefundCalculator.FEE);
    if (refund.lessThanOrEqualTo(0)) {
      return '0';
    }
    return refund.toFixed(2, Decimal.ROUND_HALF_UP);
  }

  private static share(minutesBeforeStart: number): Decimal {
    if (minutesBeforeStart <= 0) {
      return new Decimal(0);
    }
    if (minutesBeforeStart >= 24 * 60) {
      return new Decimal(1);
    }
    return new Decimal('0.5');
  }
}
