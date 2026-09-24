import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';

/** Krok 2: Move Method - zachowanie zależne od formatu przeniesione do typu Format. */
export class Format {
  static readonly TWO_D = new Format('TWO_D', 1, '2D', '25.00', false);
  static readonly THREE_D = new Format('THREE_D', 2, '3D', '32.00', true);
  static readonly IMAX = new Format('IMAX', 3, 'IMAX', '40.00', false);

  readonly basePrice: Money;

  private constructor(
    readonly name: 'TWO_D' | 'THREE_D' | 'IMAX',
    readonly code: number,
    readonly label: string,
    basePrice: string,
    readonly requiresGlasses: boolean,
  ) {
    this.basePrice = Money.of(basePrice);
  }

  static values(): readonly Format[] {
    return [Format.TWO_D, Format.THREE_D, Format.IMAX];
  }

  static fromCode(code: number): Format {
    for (const format of Format.values()) {
      if (format.code === code) {
        return format;
      }
    }
    throw new IllegalArgumentError(`unknown format code: ${code}`);
  }

  toString(): string {
    return this.name;
  }
}
