import { describe } from 'vitest';

import type { GateInput } from '../../../../src/workshop/m8/s10_qualitygate/GateInput.js';
import * as start from '../../../../src/workshop/m8/s10_qualitygate/start/QualityGate.js';
import * as step1 from '../../../../src/workshop/m8/s10_qualitygate/step1/QualityGate.js';
import * as step2 from '../../../../src/workshop/m8/s10_qualitygate/step2/QualityGate.js';
import * as step3 from '../../../../src/workshop/m8/s10_qualitygate/step3/QualityGate.js';
import * as step4 from '../../../../src/workshop/m8/s10_qualitygate/step4/QualityGate.js';
import { Scene } from '../../support/scene.js';
import { CLEAN } from './S10Fixtures.js';

/** Brak fałszywych alarmów: czysty kod przechodzi każdą wersję bramki. */
describe('S10EquivalenceTest', () => {
  describe('cleanCodePassesEveryVersionOfTheGate', () => {
    Scene.variants<GateInput, string[]>()
      .variant('start', (input) => new start.QualityGate().evaluate(input))
      .variant('step1', (input) => new step1.QualityGate().evaluate(input))
      .variant('step2', (input) => new step2.QualityGate().evaluate(input))
      .variant('step3', (input) => new step3.QualityGate().evaluate(input))
      .variant('step4', (input) => new step4.QualityGate().evaluate(input))
      .expect('czysta próbka domeny', CLEAN, [])
      .tests();
  });
}, 60_000);
