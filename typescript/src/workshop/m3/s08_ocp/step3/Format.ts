import { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';

/**
 * Krok 3 (rozwiązanie): nowy format 4DX (45.00, okulary 3D) to jedna nowa stała.
 * ScreeningOffer nie zmieniła się ani o znak - to jest OCP na wybranej osi.
 * Enum z danymi to w TypeScript klasa z instancjami statycznymi; konstruktor sam
 * dopisuje instancję do listy wszystkich formatów (odpowiednik values()).
 */
export class Format {
  private static readonly ALL: Format[] = [];

  static readonly TWO_D = new Format('2D', new Decimal('25.00'), false, '2D');
  static readonly THREE_D = new Format('3D', new Decimal('32.00'), true, '3D - okulary');
  static readonly IMAX = new Format('IMAX', new Decimal('40.00'), false, 'IMAX - ekran laserowy');
  static readonly FOUR_DX = new Format('4DX', new Decimal('45.00'), true, '4DX - ruchome fotele');

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
