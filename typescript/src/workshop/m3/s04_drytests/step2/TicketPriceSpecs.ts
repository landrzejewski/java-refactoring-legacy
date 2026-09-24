import { Decimal } from 'decimal.js';

import { LocalTime } from '../../../shared/time.js';
import type { TicketPrice } from '../TicketPrice.js';

/**
 * Krok 2 (rozwiązanie): oczekiwana cena wpisana jawnie (policzona ręcznie z regulaminu),
 * a kopia algorytmu usunięta. Wspólny zostaje tylko helper `check` - fabryka
 * przypadku i format komunikatu to dopuszczalne DRY w testach. Test znów jest
 * niezależną wyrocznią: błąd w taryfie zapala czerwone światło.
 */
export class TicketPriceSpecs {
  run(price: TicketPrice): string[] {
    const failures: string[] = [];
    this.check(price, 'normalny na wieczornym IMAX', 'IMAX', 'NORMAL', LocalTime.of(20, 0), '40.00', failures);
    this.check(price, 'student na porannym 3D', '3D', 'STUDENT', LocalTime.of(11, 0), '19.00', failures);
    this.check(price, 'senior na wieczornym 2D', '2D', 'SENIOR', LocalTime.of(18, 0), '17.50', failures);
    this.check(price, 'dziecko na porannym 2D', '2D', 'CHILD', LocalTime.of(10, 0), '10.00', failures);
    return failures;
  }

  private check(price: TicketPrice, example: string, format: string, type: string, start: LocalTime,
    expected: string, failures: string[]): void {
    const actual = price.of(format, type, start);
    if (actual.comparedTo(new Decimal(expected)) !== 0) {
      failures.push(`${example}: oczekiwano ${expected}, jest ${actual.toFixed(2)}`);
    }
  }
}
