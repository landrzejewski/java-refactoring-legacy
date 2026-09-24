import { Decimal } from 'decimal.js';

/** Krok 1: Inline Method - karnet dostał własną kopię, wciąż z cudzymi flagami. */
export class PassCounter {
  pass(entries: number): Decimal {
    const format: string = '2D';
    const pass: boolean = true;
    const morning: boolean = false;
    const ownGlasses: boolean = true;
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
    return unit.times(entries).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
