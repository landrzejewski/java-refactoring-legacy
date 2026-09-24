import { Decimal } from 'decimal.js';

/**
 * Start: fałszywa abstrakcja. Jedna "uniwersalna" metoda wycenia bilety i karnety
 * (karnet: 20.00 za wejście na seans 2D), sterowana flagami boolean. Każdy wywołujący
 * podaje flagi, które go nie dotyczą, a zmiana reguły biletów grozi zmianą karnetów.
 */
export class Pricing {
  price(format: string, quantity: number, pass: boolean,
    morning: boolean, ownGlasses: boolean): Decimal {
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
    return unit.times(quantity).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
