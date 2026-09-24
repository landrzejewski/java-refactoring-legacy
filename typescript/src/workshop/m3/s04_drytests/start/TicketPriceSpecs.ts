import { Decimal } from 'decimal.js';

import { LocalTime } from '../../../shared/time.js';
import type { TicketPrice } from '../TicketPrice.js';

/**
 * Start: specyfikacja cen "maksymalnie DRY". Jeden helper, przypadki zaszyfrowane
 * w stringach ("3D S 11"), a oczekiwana cena liczona z tej samej taryfy i tym samym
 * wzorem co kod produkcyjny. Błąd w taryfie przechodzi niezauważony: test nie ma
 * niezależnej wyroczni. Zwraca listę niespełnionych przypadków (pusta = zielono).
 *
 * W projekcie byłby to plik testowy vitest; w warsztacie leży w src,
 * żeby działał mechanizm start/stepN.
 */
export class TicketPriceSpecs {
  private static readonly SPECS = Object.freeze(['IMAX N 20', '3D S 11', '2D E 18', '2D C 10']);

  run(price: TicketPrice): string[] {
    const failures: string[] = [];
    for (const spec of TicketPriceSpecs.SPECS) {
      if (!this.check(price, spec)) {
        failures.push(spec);
      }
    }
    return failures;
  }

  private check(price: TicketPrice, spec: string): boolean {
    const [format = '', code = '', hour = ''] = spec.split(' ');
    let type: string;
    switch (code) {
      case 'S': type = 'STUDENT'; break;
      case 'E': type = 'SENIOR'; break;
      case 'C': type = 'CHILD'; break;
      default: type = 'NORMAL';
    }
    const start = LocalTime.of(Number.parseInt(hour, 10), 0);
    const base = price.tariff().basePrices.get(format)!;
    const discount = base.times(price.tariff().discountPercents.get(type)!)
      .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    const expected = base.minus(discount)
      .minus(start.hour < 12 ? new Decimal('5.00') : new Decimal(0));
    return price.of(format, type, start).comparedTo(expected) === 0;
  }
}
