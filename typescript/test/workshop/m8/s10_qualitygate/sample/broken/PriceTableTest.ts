import assert from 'node:assert/strict';

import type { TestSuite } from '../../../../../../src/workshop/m8/s10_qualitygate/GateInput.js';
import { PriceTable } from '../../../../../../src/workshop/m8/s10_qualitygate/sample/clean/PriceTable.js';

function check(condition: boolean): void {
  assert.ok(condition, 'oczekiwanie niespełnione');
}

/**
 * Fikstura dla bramki: "test", który nie przechodzi. Celowo NIE jest plikiem *.test.ts,
 * więc vitest go nie uruchamia - uruchamia go tylko QualityGate z kroku 4.
 */
export const PriceTableTest: TestSuite = {
  name: 'PriceTableTest',
  tests: {
    basePriceOfImax(): void {
      check(new PriceTable().basePrice('IMAX') === 40);
    },

    vipSurchargeStartsAtRowNine(): void {
      check(new PriceTable().vipSurcharge(9) === 10);
    },
  },
};
