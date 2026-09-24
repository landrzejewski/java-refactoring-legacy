import { describe } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import * as start from '../../../../src/workshop/m5/s01_pullupmethod/start/BoxOffice.js';
import * as step1 from '../../../../src/workshop/m5/s01_pullupmethod/step1/BoxOffice.js';
import * as step2 from '../../../../src/workshop/m5/s01_pullupmethod/step2/BoxOffice.js';
import * as step3 from '../../../../src/workshop/m5/s01_pullupmethod/step3/BoxOffice.js';
import { Scene } from '../../support/scene.js';

interface Sale {
  readonly kind: string;
  readonly title: string;
  readonly basePrice: string;
}

/** Test równoważności: start i każdy krok drukują identyczne etykiety biletów. */
describe('S01EquivalenceTest', () => {
  describe('everyStepLabelsTicketsTheSameWay', () => {
    Scene.variants<Sale, string>()
      .variant('start', (s) => new start.BoxOffice().label(s.kind, s.title, Money.of(s.basePrice)))
      .variant('step1', (s) => new step1.BoxOffice().label(s.kind, s.title, Money.of(s.basePrice)))
      .variant('step2', (s) => new step2.BoxOffice().label(s.kind, s.title, Money.of(s.basePrice)))
      .variant('step3', (s) => new step3.BoxOffice().label(s.kind, s.title, Money.of(s.basePrice)))
      .expect('normalny IMAX', { kind: 'NORMAL', title: 'Diuna', basePrice: '40.00' }, 'Diuna: 40.00')
      .expect('studencki 2D (-25%)', { kind: 'STUDENT', title: 'Amator', basePrice: '25.00' }, 'Amator: 18.75')
      .expect('VIP 3D (+10.00)', { kind: 'VIP', title: 'Kraina Lodu', basePrice: '32.00' }, 'Kraina Lodu: 42.00')
      .tests();
  });
});
