import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m3/s08_ocp/start/ScreeningOffer.js';
import * as step1 from '../../../../src/workshop/m3/s08_ocp/step1/ScreeningOffer.js';
import * as step2 from '../../../../src/workshop/m3/s08_ocp/step2/ScreeningOffer.js';
import * as step3 from '../../../../src/workshop/m3/s08_ocp/step3/ScreeningOffer.js';
import { Scene } from '../../support/scene.js';

interface Case {
  readonly format: string;
  readonly ownGlasses: boolean;
}

type Offer = start.ScreeningOffer | step1.ScreeningOffer | step2.ScreeningOffer | step3.ScreeningOffer;

function describeOffer(offer: Offer, c: Case): string {
  return `${offer.label(c.format)}: ${offer.price(c.format, c.ownGlasses).toFixed(2)}`;
}

/** Dla istniejących formatów start i każdy krok dają tę samą cenę i etykietę. */
describe('S08EquivalenceTest', () => {
  describe('existingFormatsBehaveTheSame', () => {
    Scene.variants<Case, string>()
      .variant('start', (c) => describeOffer(new start.ScreeningOffer(), c))
      .variant('step1', (c) => describeOffer(new step1.ScreeningOffer(), c))
      .variant('step2', (c) => describeOffer(new step2.ScreeningOffer(), c))
      .variant('step3', (c) => describeOffer(new step3.ScreeningOffer(), c))
      .expect('2D', { format: '2D', ownGlasses: false }, '2D: 25.00')
      .expect('3D z wypozyczeniem okularow', { format: '3D', ownGlasses: false }, '3D - okulary: 35.00')
      .expect('3D z wlasnymi okularami', { format: '3D', ownGlasses: true }, '3D - okulary: 32.00')
      .expect('IMAX', { format: 'IMAX', ownGlasses: false }, 'IMAX - ekran laserowy: 40.00')
      .tests();
  });
});
