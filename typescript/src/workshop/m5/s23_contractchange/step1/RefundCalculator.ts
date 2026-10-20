/**
 * Krok 1: czysta refaktoryzacja - Extract Method share() dla progów czasowych.
 * Nadal number (double), więc arytmetyka i zaokrąglenie są bit w bit takie same (x * 1.0 == x).
 */
export class RefundCalculator {
  refund(ticketsPaid: number, minutesBeforeStart: number): string {
    let refund = ticketsPaid * RefundCalculator.share(minutesBeforeStart) - 3.00;
    if (refund < 0) {
      refund = 0;
    }
    refund = Math.round(refund * 100) / 100.0;
    return refund.toFixed(2);
  }

  private static share(minutesBeforeStart: number): number {
    if (minutesBeforeStart <= 0) {
      return 0;
    }
    if (minutesBeforeStart >= 24 * 60) {
      return 1.0;
    }
    return 0.5;
  }
}
