import { Decimal } from 'decimal.js';

import { Format } from './Format.js';

/**
 * Krok 2: ScreeningOffer jest zamknięta na oś "format seansu" - nie zmieni się,
 * gdy dojdzie format. Nie jest zamknięta na inne osie (np. nowa dopłata za fotel
 * premium) i nie musi: zamykamy tylko oś, która faktycznie się zmienia.
 */
export class ScreeningOffer {
  private static readonly GLASSES = new Decimal('3.00');

  price(code: string, ownGlasses: boolean): Decimal {
    const format = Format.parse(code);
    const glasses = format.needsGlasses && !ownGlasses ? ScreeningOffer.GLASSES : new Decimal(0);
    return format.basePrice.plus(glasses);
  }

  label(code: string): string {
    return Format.parse(code).label;
  }
}
