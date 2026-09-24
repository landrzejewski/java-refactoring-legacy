import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m5/s02_pullupfield/start/BoxOffice.js';
import * as step1 from '../../../../src/workshop/m5/s02_pullupfield/step1/BoxOffice.js';
import * as step2 from '../../../../src/workshop/m5/s02_pullupfield/step2/BoxOffice.js';
import * as step3 from '../../../../src/workshop/m5/s02_pullupfield/step3/BoxOffice.js';
import { Scene } from '../../support/scene.js';

interface Sale {
  readonly kind: string;
  readonly seat: string;
  readonly studentId: string | null;
}

/** Test równoważności: opis biletu (z normalizacją VIP) jest identyczny w start i każdym kroku. */
describe('S02EquivalenceTest', () => {
  describe('everyStepDescribesSeatsTheSameWay', () => {
    Scene.variants<Sale, string>()
      .variant('start', (s) => new start.BoxOffice().describe(s.kind, s.seat, s.studentId))
      .variant('step1', (s) => new step1.BoxOffice().describe(s.kind, s.seat, s.studentId))
      .variant('step2', (s) => new step2.BoxOffice().describe(s.kind, s.seat, s.studentId))
      .variant('step3', (s) => new step3.BoxOffice().describe(s.kind, s.seat, s.studentId))
      .expect('normalny - miejsce bez normalizacji', { kind: 'NORMAL', seat: 'h7', studentId: null }, 'NORMAL h7')
      .expect('studencki', { kind: 'STUDENT', seat: 'F3', studentId: 'S-123' }, 'STUDENT F3 (legitymacja S-123)')
      .expect('VIP - wielkie litery', { kind: 'VIP', seat: 'k12', studentId: null }, 'VIP K12')
      .tests();
  });
});
