import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m6/s11_safecomposite/start/ComboCatalog.js';
import * as step1 from '../../../../src/workshop/m6/s11_safecomposite/step1/ComboCatalog.js';
import * as step2 from '../../../../src/workshop/m6/s11_safecomposite/step2/ComboCatalog.js';
import { Scene } from '../../support/scene.js';

/** Opis i cena zestawów bez zmian - zmienia się tylko miejsce add() w typach. */
describe('S11EquivalenceTest', () => {
  describe('everyStepDescribesCombosTheSame', () => {
    Scene.variants<string, string>()
      .variant('start', (c) => new start.ComboCatalog().find(c).describe())
      .variant('step1', (c) => new step1.ComboCatalog().find(c).describe())
      .variant('step2', (c) => new step2.ComboCatalog().find(c).describe())
      .expect('zestaw rodzinny', 'family',
        'Zestaw Rodzinny 49.00 [Popcorn XL 24.00, Napoje 25.00 [Cola 9.00, Cola 9.00, Woda 7.00]]')
      .expect('zestaw duo', 'duo', 'Zestaw Duo 36.00 [Popcorn L 18.00, Cola 9.00, Cola 9.00]')
      .expect('pojedynczy produkt', 'nachos', 'Nachos 14.00')
      .tests();
  });
});
