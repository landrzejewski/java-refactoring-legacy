import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m5/s05_extractsubclass/start/Programme.js';
import * as step1 from '../../../../src/workshop/m5/s05_extractsubclass/step1/Programme.js';
import * as step2 from '../../../../src/workshop/m5/s05_extractsubclass/step2/Programme.js';
import * as step3 from '../../../../src/workshop/m5/s05_extractsubclass/step3/Programme.js';
import * as step4 from '../../../../src/workshop/m5/s05_extractsubclass/step4/Programme.js';
import { Scene } from '../../support/scene.js';

interface Entry {
  readonly title: string;
  readonly format: string;
  readonly guest: string | null;
}

/** Test równoważności: linia repertuaru (opis i cena) jest identyczna w start i każdym kroku. */
describe('S05EquivalenceTest', () => {
  describe('everyStepPrintsTheSameProgramme', () => {
    Scene.variants<Entry, string>()
      .variant('start', (e) => new start.Programme().line(e.title, e.format, e.guest))
      .variant('step1', (e) => new step1.Programme().line(e.title, e.format, e.guest))
      .variant('step2', (e) => new step2.Programme().line(e.title, e.format, e.guest))
      .variant('step3', (e) => new step3.Programme().line(e.title, e.format, e.guest))
      .variant('step4', (e) => new step4.Programme().line(e.title, e.format, e.guest))
      .expect('zwykły seans IMAX', { title: 'Diuna', format: 'IMAX', guest: null }, 'Diuna (IMAX) | 40.00')
      .expect('premiera 2D', { title: 'Amator', format: '2D', guest: 'Anna Nowak' },
        'Amator (2D) - premiera, gość: Anna Nowak | 40.00')
      .expect('premiera 3D', { title: 'Kraina Lodu', format: '3D', guest: 'Jan Kowalski' },
        'Kraina Lodu (3D) - premiera, gość: Jan Kowalski | 47.00')
      .tests();
  });
});
