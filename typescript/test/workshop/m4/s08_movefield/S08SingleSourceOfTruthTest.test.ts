import { describe, expect, it } from 'vitest';

import * as startHall from '../../../../src/workshop/m4/s08_movefield/start/Hall.js';
import * as startScreening from '../../../../src/workshop/m4/s08_movefield/start/Screening.js';
import * as step3Hall from '../../../../src/workshop/m4/s08_movefield/step3/Hall.js';
import * as step3Screening from '../../../../src/workshop/m4/s08_movefield/step3/Screening.js';

/** Po co Move Field: w start dwa seanse w TEJ SAMEJ sali mogą się nie zgadzać co do VIP. */
describe('S08SingleSourceOfTruthTest', () => {
  it('startLetsScreeningsInOneHallDisagree', () => {
    const hall = new startHall.Hall('Sala 1');
    const evening = new startScreening.Screening(hall, 3, 10);
    const morning = new startScreening.Screening(hall, 1, 8);
    // Ten sam fotel raz jest VIP, raz nie.
    expect(evening.isVip(9)).not.toBe(morning.isVip(9));
  });

  it('afterMoveFieldTheHallDecidesForEveryScreening', () => {
    const hall = new step3Hall.Hall('Sala 1', 10);
    const evening = new step3Screening.Screening(hall, 3);
    const morning = new step3Screening.Screening(hall, 1);
    expect(evening.isVip(9)).toBe(morning.isVip(9));
    expect(evening.isVip(10)).toBe(morning.isVip(10));
  });
});
