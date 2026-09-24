import { describe } from 'vitest';

import { Hall } from '../../../../src/workshop/m3/s05_kiss/Hall.js';
import * as start from '../../../../src/workshop/m3/s05_kiss/start/SeatCounter.js';
import * as step1 from '../../../../src/workshop/m3/s05_kiss/step1/SeatCounter.js';
import * as step2 from '../../../../src/workshop/m3/s05_kiss/step2/SeatCounter.js';
import { Scene } from '../../support/scene.js';

/** Prostsza wersja liczy wolne miejsca dokładnie tak samo jak "sprytna". */
describe('S05EquivalenceTest', () => {
  describe('everyStepCountsFreeSeatsTheSame', () => {
    const startCounter = new start.SeatCounter();
    const step1Counter = new step1.SeatCounter();
    const step2Counter = new step2.SeatCounter();
    Scene.variants<Hall, string>()
      .variant('start', (hall) => startCounter.summary(hall))
      .variant('step1', (hall) => step1Counter.summary(hall))
      .variant('step2', (hall) => step2Counter.summary(hall))
      .expect('zajete, zablokowane i przejscie nie sa wolne',
        new Hall(['..X', 'XXX', '.X.', '. B.'], 3), 'wolne: 6, wolne VIP: 4')
      .expect('sala bez rzedow VIP', new Hall(['....', '....'], 10), 'wolne: 8, wolne VIP: 0')
      .expect('cala sala VIP', new Hall(['X.', '.X'], 1), 'wolne: 2, wolne VIP: 2')
      .expect('pusta lista rzedow', new Hall([], 10), 'wolne: 0, wolne VIP: 0')
      .tests();
  });
});
