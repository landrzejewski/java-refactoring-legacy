import assert from 'node:assert/strict';

import type { TestSuite } from '../../../../../../src/workshop/m2/s04_qualitygate/GateInput.js';
import { PriceTable } from '../../../../../../src/workshop/m2/s04_qualitygate/sample/clean/PriceTable.js';

/** Test próbki czystej: każda publiczna metoda ma przypadek. */
export const PriceTableTest: TestSuite = {
  name: 'PriceTableTest',
  tests: {
    basePriceDependsOnFormat(): void {
      assert.equal(new PriceTable().basePrice('3D'), 32);
    },

    vipSurchargeFromRowTen(): void {
      assert.equal(new PriceTable().vipSurcharge(9), 0);
      assert.equal(new PriceTable().vipSurcharge(10), 10);
    },
  },
};
