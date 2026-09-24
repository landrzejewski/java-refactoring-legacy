import { Decimal } from 'decimal.js';

import { LocalTime } from '../../../shared/time.js';
import type { Tariff } from '../Tariff.js';
import type { TicketPrice } from '../TicketPrice.js';

/**
 * Krok 1: DAMP zamiast szyfru - każdy przypadek ma nazwę i jawne dane wejściowe,
 * a komunikat mówi, co się nie zgadza. Czytelniej, ale oczekiwanie nadal liczy
 * `expectedFromTariff`, czyli kopia algorytmu produkcyjnego.
 */
export class TicketPriceSpecs {
  run(price: TicketPrice): string[] {
    const failures: string[] = [];
    this.check(price, 'normalny na wieczornym IMAX', 'IMAX', 'NORMAL', LocalTime.of(20, 0), failures);
    this.check(price, 'student na porannym 3D', '3D', 'STUDENT', LocalTime.of(11, 0), failures);
    this.check(price, 'senior na wieczornym 2D', '2D', 'SENIOR', LocalTime.of(18, 0), failures);
    this.check(price, 'dziecko na porannym 2D', '2D', 'CHILD', LocalTime.of(10, 0), failures);
    return failures;
  }

  private check(price: TicketPrice, example: string, format: string, type: string, start: LocalTime,
    failures: string[]): void {
    const expected = this.expectedFromTariff(price.tariff(), format, type, start);
    const actual = price.of(format, type, start);
    if (actual.comparedTo(expected) !== 0) {
      failures.push(`${example}: oczekiwano ${expected.toFixed(2)}, jest ${actual.toFixed(2)}`);
    }
  }

  private expectedFromTariff(tariff: Tariff, format: string, type: string, start: LocalTime): Decimal {
    const base = tariff.basePrices.get(format)!;
    const discount = base.times(tariff.discountPercents.get(type)!)
      .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    const morning = start.hour < 12 ? new Decimal('5.00') : new Decimal(0);
    return base.minus(discount).minus(morning);
  }
}
