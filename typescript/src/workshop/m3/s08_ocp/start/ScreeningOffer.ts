import { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';

/**
 * Start: wiedza o formatach seansu rozsiana po kilku switchach na stringu.
 * Każdy nowy format (kino kupuje salę 4DX) wymaga edycji wszystkich switchy,
 * a kompilator nie podpowie, o którym zapomnieliśmy - wpadnie do default.
 */
export class ScreeningOffer {
  price(format: string, ownGlasses: boolean): Decimal {
    let base: Decimal;
    switch (format) {
      case '2D': base = new Decimal('25.00'); break;
      case '3D': base = new Decimal('32.00'); break;
      case 'IMAX': base = new Decimal('40.00'); break;
      default: throw new IllegalArgumentError(`nieznany format: ${format}`);
    }
    let glasses: Decimal;
    switch (format) {
      case '3D': glasses = ownGlasses ? new Decimal(0) : new Decimal('3.00'); break;
      default: glasses = new Decimal(0);
    }
    return base.plus(glasses);
  }

  label(format: string): string {
    switch (format) {
      case '2D': return '2D';
      case '3D': return '3D - okulary';
      case 'IMAX': return 'IMAX - ekran laserowy';
      default: throw new IllegalArgumentError(`nieznany format: ${format}`);
    }
  }
}
