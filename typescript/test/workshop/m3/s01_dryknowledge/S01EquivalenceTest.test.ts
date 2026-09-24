import { describe } from 'vitest';

import { LocalTime } from '../../../../src/workshop/shared/time.js';
import * as start from '../../../../src/workshop/m3/s01_dryknowledge/start/BoxOffice.js';
import * as step1 from '../../../../src/workshop/m3/s01_dryknowledge/step1/BoxOffice.js';
import * as step2 from '../../../../src/workshop/m3/s01_dryknowledge/step2/BoxOffice.js';
import * as step3 from '../../../../src/workshop/m3/s01_dryknowledge/step3/BoxOffice.js';
import * as step4 from '../../../../src/workshop/m3/s01_dryknowledge/step4/BoxOffice.js';
import { Ticket } from '../../../../src/workshop/m3/s01_dryknowledge/Ticket.js';
import { Scene } from '../../support/scene.js';

interface Case {
  readonly ticket: Ticket;
  readonly hoursBeforeStart: number;
}

type Office = start.BoxOffice | step1.BoxOffice | step2.BoxOffice | step3.BoxOffice | step4.BoxOffice;

function run(office: Office, c: Case): string {
  return `${office.sell(c.ticket).toFixed(2)} / ${office.refund(c.ticket, c.hoursBeforeStart).toFixed(2)}`;
}

/** Start i każdy krok: ta sama cena sprzedaży i ta sama kwota zwrotu. */
describe('S01EquivalenceTest', () => {
  describe('everyStepSellsAndRefundsTheSame', () => {
    Scene.variants<Case, string>()
      .variant('start', (c) => run(new start.BoxOffice(), c))
      .variant('step1', (c) => run(new step1.BoxOffice(), c))
      .variant('step2', (c) => run(new step2.BoxOffice(), c))
      .variant('step3', (c) => run(new step3.BoxOffice(), c))
      .variant('step4', (c) => run(new step4.BoxOffice(), c))
      .expect('IMAX normalny, zwrot 48h przed: 100% - 3.00',
        { ticket: new Ticket('IMAX', 'NORMAL', LocalTime.of(20, 0)), hoursBeforeStart: 48 }, '40.00 / 37.00')
      .expect('3D student rano, zwrot 10h przed: 50% - 3.00',
        { ticket: new Ticket('3D', 'STUDENT', LocalTime.of(11, 0)), hoursBeforeStart: 10 }, '19.00 / 6.50')
      .expect('2D senior, zwrot po starcie: 0, nie ponizej zera',
        { ticket: new Ticket('2D', 'SENIOR', LocalTime.of(18, 0)), hoursBeforeStart: 0 }, '17.50 / 0.00')
      .expect('2D dziecko rano, zwrot dokladnie 24h przed',
        { ticket: new Ticket('2D', 'CHILD', LocalTime.of(10, 0)), hoursBeforeStart: 24 }, '10.00 / 7.00')
      .expect('3D senior wieczorem, zwrot 1h przed',
        { ticket: new Ticket('3D', 'SENIOR', LocalTime.of(21, 15)), hoursBeforeStart: 1 }, '22.40 / 8.20')
      .tests();
  });
});
