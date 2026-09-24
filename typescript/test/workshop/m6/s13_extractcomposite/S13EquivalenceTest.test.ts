import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m6/s13_extractcomposite/start/ProgramCatalog.js';
import * as step1 from '../../../../src/workshop/m6/s13_extractcomposite/step1/ProgramCatalog.js';
import * as step2 from '../../../../src/workshop/m6/s13_extractcomposite/step2/ProgramCatalog.js';
import { Scene } from '../../support/scene.js';

/** Czas i opis programów - w tym zagnieżdżenie i pusty kontener. */
describe('S13EquivalenceTest', () => {
  describe('everyStepDescribesProgramsTheSame', () => {
    Scene.variants<string, string>()
      .variant('start', (c) => new start.ProgramCatalog().find(c).describe())
      .variant('step1', (c) => new step1.ProgramCatalog().find(c).describe())
      .variant('step2', (c) => new step2.ProgramCatalog().find(c).describe())
      .expect('maraton z przerwą', 'marathon',
        'Maraton Diuna (336 min) [Diuna (155 min), Diuna: Czesc druga (166 min)]')
      .expect('blok bez przerw', 'shorts',
        'Blok Krotkie metraze (36 min) [Kot (12 min), Pies (9 min), Ryba (15 min)]')
      .expect('zagnieżdżenie', 'night',
        'Maraton Noc kina (171 min) [Blok Krotkie metraze (36 min) '
          + '[Kot (12 min), Pies (9 min), Ryba (15 min)], Amator (120 min)]')
      .expect('pusty maraton', 'empty', 'Maraton Pusty (0 min) []')
      .tests();
  });
});
