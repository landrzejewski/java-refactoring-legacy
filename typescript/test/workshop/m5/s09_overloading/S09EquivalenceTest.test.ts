import { describe } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import * as startPrices from '../../../../src/workshop/m5/s09_overloading/start/PriceList.js';
import * as startStudent from '../../../../src/workshop/m5/s09_overloading/start/StudentTicket.js';
import * as step1Prices from '../../../../src/workshop/m5/s09_overloading/step1/PriceList.js';
import * as step1Student from '../../../../src/workshop/m5/s09_overloading/step1/StudentTicket.js';
import * as step2Prices from '../../../../src/workshop/m5/s09_overloading/step2/PriceList.js';
import * as step2Student from '../../../../src/workshop/m5/s09_overloading/step2/StudentTicket.js';
import { Scene } from '../../support/scene.js';

/**
 * Wspólna część wszystkich wariantów: wywołanie z typem DEKLAROWANYM StudentTicket.
 * Tak wyglądał klient przed Extract Superclass - i tu wszystkie wersje są zgodne.
 * (W start klient z typem StudentTicket wołał wariant priceStudent - w Javie to samo robił kompilator.)
 */
/**
 * Start woła metodę po nazwie: po `jump`/`next` w start jest już tylko `price` (w Javie to wciąż
 * to samo wywołanie price(...) dzięki przeciążeniom), a test ma się nadal typować.
 */
function priceStudentOf(prices: object, ticket: unknown): string {
  const methods = prices as Record<string, ((ticket: unknown) => unknown) | undefined>;
  const price = methods['priceStudent'] ?? methods['price'];
  return String(price!.call(prices, ticket));
}

describe('S09EquivalenceTest', () => {
  describe('everyStepPricesStaticallyTypedStudentTicket', () => {
    Scene.variants<string, string>()
      .variant('start', (p) => priceStudentOf(new startPrices.PriceList(),
        new startStudent.StudentTicket('Amator', Money.of(p))))
      .variant('step1', (p) => new step1Prices.PriceList()
        .price(new step1Student.StudentTicket('Amator', Money.of(p))).toString())
      .variant('step2', (p) => new step2Prices.PriceList()
        .price(new step2Student.StudentTicket('Amator', Money.of(p))).toString())
      .expect('studencki 2D', '25.00', '18.75')
      .expect('studencki IMAX', '40.00', '30.00')
      .tests();
  });
});
