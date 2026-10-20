/**
 * Start: zwrot za anulowaną rezerwację na number (double) - dokładnie tak jak w starym CinemaManager.
 * >= 24h przed seansem 100%, mniej 50%, po starcie 0; potrącenie 3.00, nie poniżej zera.
 * Kontrakt to także FORMAT wyniku: zawsze dwa miejsca po przecinku, np. "0.00".
 */
export class RefundCalculator {
  refund(ticketsPaid: number, minutesBeforeStart: number): string {
    let refund: number;
    if (minutesBeforeStart <= 0) {
      refund = 0;
    } else if (minutesBeforeStart >= 24 * 60) {
      refund = ticketsPaid;
    } else {
      refund = ticketsPaid * 0.5;
    }
    refund = refund - 3.00;
    if (refund < 0) {
      refund = 0;
    }
    refund = Math.round(refund * 100) / 100.0;
    return refund.toFixed(2);
  }
}
