import type { Decimal } from 'decimal.js';
import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m7/s04_removeduplication/start/BoxOffice.js';
import * as startWeb from '../../../../src/workshop/m7/s04_removeduplication/start/WebShop.js';
import * as step1 from '../../../../src/workshop/m7/s04_removeduplication/step1/BoxOffice.js';
import * as step1Web from '../../../../src/workshop/m7/s04_removeduplication/step1/WebShop.js';
import * as step2 from '../../../../src/workshop/m7/s04_removeduplication/step2/BoxOffice.js';
import * as step2Web from '../../../../src/workshop/m7/s04_removeduplication/step2/WebShop.js';
import * as step3 from '../../../../src/workshop/m7/s04_removeduplication/step3/BoxOffice.js';
import * as step3Web from '../../../../src/workshop/m7/s04_removeduplication/step3/WebShop.js';
import { Scene } from '../../support/scene.js';
import { EDGE, prices, TEN_2D, TWO_3D } from './baskets.js';

/**
 * Test równoważności dla przypadków, w których kasa i sklep od zawsze się zgadzają.
 * Przypadek brzegowy zaokrąglenia jest w S04RoundingDecisionTest.
 */
describe('S04EquivalenceTest', () => {
  describe('boxOfficeNeverChanges', () => {
    Scene.variants<readonly Decimal[], string>()
      .variant('start', (p) => new start.BoxOffice().total(p).toFixed(2))
      .variant('step1', (p) => new step1.BoxOffice().total(p).toFixed(2))
      .variant('step2', (p) => new step2.BoxOffice().total(p).toFixed(2))
      .variant('step3', (p) => new step3.BoxOffice().total(p).toFixed(2))
      .expect('dwa bilety 3D', TWO_3D, '64.00')
      .expect('9 biletow - jeszcze bez rabatu', prices('25.00', 9), '225.00')
      .expect('10 biletow 2D', TEN_2D, '225.00')
      .expect('rabat z koncowka 5: 231.25 -> 208.12', EDGE, '208.12')
      .expect('pusty koszyk', [], '0.00')
      .tests();
  });

  describe('webShopAgreesOnOrdinaryBaskets', () => {
    Scene.variants<readonly Decimal[], string>()
      .variant('start', (p) => new startWeb.WebShop().total(p).toFixed(2))
      .variant('step1', (p) => new step1Web.WebShop().total(p).toFixed(2))
      .variant('step2', (p) => new step2Web.WebShop().total(p).toFixed(2))
      .variant('step3', (p) => new step3Web.WebShop().total(p).toFixed(2))
      .expect('dwa bilety 3D + oplaty', TWO_3D, '68.00')
      .expect('10 biletow 2D + oplaty', TEN_2D, '245.00')
      .expect('pusty koszyk', [], '0.00')
      .tests();
  });
});
