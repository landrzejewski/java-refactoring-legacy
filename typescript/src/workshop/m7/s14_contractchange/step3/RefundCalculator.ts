import { Decimal } from 'decimal.js';

/**
 * Krok 3 (rozwiązanie): świadoma decyzja zamiast "przy okazji".
 * - Format "0.00" to kontrakt (paragony, e-maile) - przywrócony: obcięcie do zera (max)
 *   PRZED formatowaniem, jedna ścieżka wyniku.
 * - Zaokrąglenie HALF_UP na Decimal to reguła domeny; różnica groszowa względem number
 *   została uzgodniona z księgowością i zatwierdzona jako ZMIANA KONTRAKTU w osobnym commicie
 *   (test S14ContractTest ma dla niej jawnie nowe oczekiwanie).
 */
export class RefundCalculator {
  private static readonly FEE = new Decimal('3.00');

  refund(ticketsPaid: number, minutesBeforeStart: number): string {
    const refund = Decimal.max(
      new Decimal(ticketsPaid)
        .times(RefundCalculator.share(minutesBeforeStart))
        .minus(RefundCalculator.FEE),
      0);
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
