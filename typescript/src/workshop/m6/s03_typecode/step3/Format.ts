import { Money } from '../../../shared/Money.js';

/**
 * Krok 3: typ domenowy bez wiedzy o kodach trwałych - mapowanie liczb przeniesione
 * do mappera FormatCodes (granica trwałości).
 */
export class Format {
  static readonly TWO_D = new Format('TWO_D', '2D', '25.00', false);
  static readonly THREE_D = new Format('THREE_D', '3D', '32.00', true);
  static readonly IMAX = new Format('IMAX', 'IMAX', '40.00', false);

  readonly basePrice: Money;

  private constructor(
    readonly name: 'TWO_D' | 'THREE_D' | 'IMAX',
    readonly label: string,
    basePrice: string,
    readonly requiresGlasses: boolean,
  ) {
    this.basePrice = Money.of(basePrice);
  }

  static values(): readonly Format[] {
    return [Format.TWO_D, Format.THREE_D, Format.IMAX];
  }

  toString(): string {
    return this.name;
  }
}
