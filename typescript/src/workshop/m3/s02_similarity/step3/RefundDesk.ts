import { Decimal } from 'decimal.js';

/**
 * Krok 3 (rozwiązanie): potrącenie przy zwrocie to reguła regulaminu zwrotów
 * (właściciel: obsługa klienta, prawnik). Własna stała, własny powód zmiany.
 */
export class RefundDesk {
  private static readonly REFUND_DEDUCTION = new Decimal('3.00');

  refund(paidForTickets: Decimal, percent: number): Decimal {
    const share = paidForTickets.times(percent)
      .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    return Decimal.max(share.minus(RefundDesk.REFUND_DEDUCTION), 0);
  }
}
