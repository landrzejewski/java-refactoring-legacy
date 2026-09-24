import { Decimal } from 'decimal.js';

/**
 * Krok 1: Inline Method (wszystkie wywołania, usuń Pricing). Kod wspólnej metody
 * wrócił do wywołującego razem z flagami - tymczasowo nieładnie, ale bezpiecznie.
 */
export class TicketCounter {
  ticket(format: string, morning: boolean, ownGlasses: boolean): Decimal {
    const pass: boolean = false;
    let unit: Decimal;
    if (pass) {
      unit = new Decimal('20.00');
    } else {
      switch (format) {
        case 'IMAX': unit = new Decimal('40.00'); break;
        case '3D': unit = new Decimal('32.00'); break;
        default: unit = new Decimal('25.00');
      }
      if (morning) {
        unit = unit.minus(new Decimal('5.00'));
      }
    }
    if (format === '3D' && !ownGlasses && !pass) {
      unit = unit.plus(new Decimal('3.00'));
    }
    return unit.times(1).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
