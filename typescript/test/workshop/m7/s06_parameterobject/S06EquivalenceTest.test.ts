import { describe } from 'vitest';

import * as step1 from '../../../../src/workshop/m7/s06_parameterobject/step1/ScreeningPlanner.js';
import * as step2 from '../../../../src/workshop/m7/s06_parameterobject/step2/ScreeningPlanner.js';
import * as step3 from '../../../../src/workshop/m7/s06_parameterobject/step3/ScreeningPlanner.js';
import { Scene } from '../../support/scene.js';
import { Clump, DAY, runStart, runStep1, runStep2, runStep3 } from './S06Observations.js';

/** Test równoważności dla poprawnych terminów: nowe API (od kroku 1) i stare sygnatury. */
function cases(scene: Scene<Clump, string>): void {
  scene
    .expect('IMAX w sali 1', new Clump('S1', DAY, 1, 'IMAX'), 'S1 2026-03-10 sala 1 (IMAX) | 40.00')
    .expect('3D w sali 2', new Clump('S2', DAY, 2, '3D'), 'S2 2026-03-10 sala 2 (3D) | 32.00')
    .expect('2D w sali 8 (granica)', new Clump('S3', DAY, 8, '2D'), 'S3 2026-03-10 sala 8 (2D) | 25.00')
    .tests();
}

describe('S06EquivalenceTest', () => {
  describe('newApiBehavesLikeTheOldOne', () => {
    cases(Scene.variants<Clump, string>()
      .variant('start', runStart)
      .variant('step1', runStep1)
      .variant('step2', runStep2)
      .variant('step3', runStep3));
  });

  // Wywołania przestarzałych sygnatur (@deprecated) - edytor je przekreśla, ale działają.
  describe('deprecatedSignaturesStillWork', () => {
    const planner1 = new step1.ScreeningPlanner();
    const planner2 = new step2.ScreeningPlanner();
    const planner3 = new step3.ScreeningPlanner();
    cases(Scene.variants<Clump, string>()
      .variant('step1', (c) => `${planner1.describe(c.screeningId, c.date, c.hall, c.format)}`
        + ` | ${planner1.ticketPrice(c.screeningId, c.date, c.hall, c.format).toFixed(2)}`)
      .variant('step2', (c) => `${planner2.describe(c.screeningId, c.date, c.hall, c.format)}`
        + ` | ${planner2.ticketPrice(c.screeningId, c.date, c.hall, c.format).toFixed(2)}`)
      .variant('step3', (c) => `${planner3.describe(c.screeningId, c.date, c.hall, c.format)}`
        + ` | ${planner3.ticketPrice(c.screeningId, c.date, c.hall, c.format).toFixed(2)}`));
  });
});
