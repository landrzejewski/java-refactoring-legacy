import assert from 'node:assert/strict';

import type { TestSuite } from '../../../../../../src/workshop/m2/s04_qualitygate/GateInput.js';
import { PriceTable } from '../../../../../../src/workshop/m2/s04_qualitygate/sample/dirty/PriceTable.js';

/** Test próbki "brudnej": zielony, ale sprawdza tylko basePrice (reszta bez pokrycia). */
export const PriceTableTest: TestSuite = {
  name: 'PriceTableTest',
  tests: {
    basePriceDependsOnFormat(): void {
      assert.equal(new PriceTable().basePrice('IMAX'), 40);
      assert.equal(new PriceTable().basePrice('2D'), 25);
    },
  },
};
