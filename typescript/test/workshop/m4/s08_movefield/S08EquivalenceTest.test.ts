import { describe } from 'vitest';

import { SeatQuery } from '../../../../src/workshop/m4/s08_movefield/SeatQuery.js';
import * as startHall from '../../../../src/workshop/m4/s08_movefield/start/Hall.js';
import * as startScreening from '../../../../src/workshop/m4/s08_movefield/start/Screening.js';
import * as start from '../../../../src/workshop/m4/s08_movefield/start/SeatPricer.js';
import * as step1Hall from '../../../../src/workshop/m4/s08_movefield/step1/Hall.js';
import * as step1Screening from '../../../../src/workshop/m4/s08_movefield/step1/Screening.js';
import * as step1 from '../../../../src/workshop/m4/s08_movefield/step1/SeatPricer.js';
import * as step2Hall from '../../../../src/workshop/m4/s08_movefield/step2/Hall.js';
import * as step2Screening from '../../../../src/workshop/m4/s08_movefield/step2/Screening.js';
import * as step2 from '../../../../src/workshop/m4/s08_movefield/step2/SeatPricer.js';
import * as step3Hall from '../../../../src/workshop/m4/s08_movefield/step3/Hall.js';
import * as step3Screening from '../../../../src/workshop/m4/s08_movefield/step3/Screening.js';
import * as step3 from '../../../../src/workshop/m4/s08_movefield/step3/SeatPricer.js';
import { Scene } from '../../support/scene.js';

/**
 * Test równoważności: ta sama wycena miejsca. Adaptery start/step1 podają próg do Screening,
 * a step2/step3 do Hall - to widoczna w teście zmiana konstruktorów po Move Field.
 */
describe('S08EquivalenceTest', () => {
  describe('everyStepQuotesSeatsTheSameWay', () => {
    Scene.variants<SeatQuery, string>()
      .variant('start', (q) => new start.SeatPricer().quote(
        new startScreening.Screening(new startHall.Hall(q.hall), q.format, q.vipFromRow), q.row))
      .variant('step1', (q) => new step1.SeatPricer().quote(
        new step1Screening.Screening(new step1Hall.Hall(q.hall), q.format, q.vipFromRow), q.row))
      .variant('step2', (q) => new step2.SeatPricer().quote(
        new step2Screening.Screening(new step2Hall.Hall(q.hall, q.vipFromRow), q.format), q.row))
      .variant('step3', (q) => new step3.SeatPricer().quote(
        new step3Screening.Screening(new step3Hall.Hall(q.hall, q.vipFromRow), q.format), q.row))
      .expect('IMAX, rząd 10 przy progu 10 - VIP', new SeatQuery('Sala 1', 10, 3, 10),
        'Sala 1, rzad 10 (VIP): 50.00')
      .expect('3D, rząd 9 przy progu 10', new SeatQuery('Sala 1', 10, 2, 9), 'Sala 1, rzad 9: 32.00')
      .expect('2D, mała sala z progiem 8 - rząd 9 to VIP', new SeatQuery('Sala 2', 8, 1, 9),
        'Sala 2, rzad 9 (VIP): 35.00')
      .tests();
  });
});
