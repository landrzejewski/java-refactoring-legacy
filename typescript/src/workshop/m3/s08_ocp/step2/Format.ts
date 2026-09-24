import { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';

/**
 * Krok 2: wiedza o formacie przeniesiona do enuma (Move Method / Replace Conditional
 * with Polymorphism w wersji "dane zamiast gałęzi"). Nowy format = jedna linia tutaj.
 * Enum z danymi to w TypeScript klasa z instancjami statycznymi; konstruktor sam
 * dopisuje instancję do listy wszystkich formatów (odpowiednik values()).
 */
export class Format {
  private static readonly ALL: Format[] = [];

  static readonly TWO_D = new Format('2D', new Decimal('25.00'), false, '2D');
  static readonly THREE_D = new Format('3D', new Decimal('32.00'), true, '3D - okulary');
  static readonly IMAX = new Format('IMAX', new Decimal('40.00'), false, 'IMAX - ekran laserowy');

  private constructor(
    private readonly code: string,
    readonly basePrice: Decimal,
    readonly needsGlasses: boolean,
    readonly label: string,
  ) {
    Format.ALL.push(this);
  }

  static values(): readonly Format[] {
    return Object.freeze([...Format.ALL]);
  }

  static parse(code: string): Format {
    for (const format of Format.ALL) {
      if (format.code === code) {
        return format;
      }
    }
    throw new IllegalArgumentError(`nieznany format: ${code}`);
  }
}
