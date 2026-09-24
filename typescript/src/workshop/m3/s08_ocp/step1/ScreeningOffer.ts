import { Decimal } from 'decimal.js';

import { assertNever } from '../../../../shared/assertNever.js';
import { Format, parseFormat } from './Format.js';

/**
 * Krok 1: switch na enumie z assertNever zamiast default. To jeszcze nie OCP, ale już
 * bezpieczniejszy zamknięty zbiór: nowa wartość enuma = błąd kompilacji w każdym switchu,
 * który jej nie obsłuży. Dla małego, stabilnego zbioru to bywa wystarczający model.
 */
export class ScreeningOffer {
  price(code: string, ownGlasses: boolean): Decimal {
    const format = parseFormat(code);
    let base: Decimal;
    switch (format) {
      case Format.TWO_D: base = new Decimal('25.00'); break;
      case Format.THREE_D: base = new Decimal('32.00'); break;
      case Format.IMAX: base = new Decimal('40.00'); break;
      default: return assertNever(format);
    }
    let glasses: Decimal;
    switch (format) {
      case Format.THREE_D: glasses = ownGlasses ? new Decimal(0) : new Decimal('3.00'); break;
      case Format.TWO_D:
      case Format.IMAX: glasses = new Decimal(0); break;
      default: return assertNever(format);
    }
    return base.plus(glasses);
  }

  label(code: string): string {
    const format = parseFormat(code);
    switch (format) {
      case Format.TWO_D: return '2D';
      case Format.THREE_D: return '3D - okulary';
      case Format.IMAX: return 'IMAX - ekran laserowy';
      default: return assertNever(format);
    }
  }
}
