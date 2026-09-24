import { describe, expect, it } from 'vitest';

import * as start from '../../../../src/workshop/m7/s04_removeduplication/start/WebShop.js';
import * as step1 from '../../../../src/workshop/m7/s04_removeduplication/step1/WebShop.js';
import * as step2 from '../../../../src/workshop/m7/s04_removeduplication/step2/WebShop.js';
import * as step3 from '../../../../src/workshop/m7/s04_removeduplication/step3/WebShop.js';
import { EDGE } from './baskets.js';

/**
 * Dokumentuje różnicę ukrytą w duplikacie: 3 x student 2D (18.75) + 7 x normalny (25.00)
 * = 231.25, rabat 23.125. Kasa (HALF_UP) odejmuje 23.13, sklep (HALF_EVEN) 23.12.
 * Ujednolicenie trybu to zmiana kontraktu sklepu - osobny, świadomy krok 2.
 */
describe('S04RoundingDecisionTest', () => {
  it('startAndStep1KeepTheHistoricalWebRounding', () => {
    expect(new start.WebShop().total(EDGE).toFixed(2)).toBe('228.13');
    expect(new step1.WebShop().total(EDGE).toFixed(2)).toBe('228.13');
  });

  it('step2DeliberatelyAlignsWebWithBoxOffice', () => {
    expect(new step2.WebShop().total(EDGE).toFixed(2)).toBe('228.12');
    expect(new step3.WebShop().total(EDGE).toFixed(2)).toBe('228.12');
  });
});
