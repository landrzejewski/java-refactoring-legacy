import { describe, expect, it } from 'vitest';

import * as start from '../../../../src/workshop/m4/s02_extractvariable/start/TicketPrice.js';
import * as step1 from '../../../../src/workshop/m4/s02_extractvariable/step1/TicketPrice.js';
import * as step2 from '../../../../src/workshop/m4/s02_extractvariable/step2/TicketPrice.js';
import * as step3 from '../../../../src/workshop/m4/s02_extractvariable/step3/TicketPrice.js';
import { TicketRequest } from '../../../../src/workshop/m4/s02_extractvariable/TicketRequest.js';
import { LocalTime } from '../../../../src/workshop/shared/time.js';
import { Scene } from '../../support/scene.js';

const FREE_SEATING = new TicketRequest(2, 'E', LocalTime.of(18, 0), null, true);

/** Test równoważności: start i każdy krok liczą tę samą cenę biletu. */
describe('S02EquivalenceTest', () => {
  describe('everyStepPricesTicketsTheSameWay', () => {
    Scene.variants<TicketRequest, string>()
      .variant('start', (r) => new start.TicketPrice().price(r).toFixed(2))
      .variant('step1', (r) => new step1.TicketPrice().price(r).toFixed(2))
      .variant('step2', (r) => new step2.TicketPrice().price(r).toFixed(2))
      .variant('step3', (r) => new step3.TicketPrice().price(r).toFixed(2))
      .expect('IMAX normalny wieczorem',
        new TicketRequest(3, 'N', LocalTime.of(20, 0), 5, false), '40.00')
      .expect('3D student rano, VIP, bez okularów',
        new TicketRequest(2, 'S', LocalTime.of(10, 0), 12, false), '32.00')
      .expect('3D senior, wolna widownia (row = null), własne okulary', FREE_SEATING, '22.40')
      .expect('2D dziecko 11:59, rząd 10 to już VIP',
        new TicketRequest(1, 'C', LocalTime.of(11, 59), 10, false), '20.00')
      .expect('3D normalny 12:00 to już nie poranek, rząd 9',
        new TicketRequest(2, 'N', LocalTime.of(12, 0), 9, false), '35.00')
      .expect('IMAX dziecko rano, wolna widownia',
        new TicketRequest(3, 'C', LocalTime.of(9, 0), null, false), '19.00')
      .tests();
  });

  /**
   * Dokumentuje pułapkę: wydzielenie samego porównania bez osłony null zmienia zachowanie.
   * W Javie unboxing null rzuca NullPointerException. W TypeScript takie wydzielenie odrzuca
   * kompilator (TS18047), a obejście go przez `!` nie rzuca wyjątku - JS po cichu zamienia null na 0.
   */
  it('extractingTheComparisonWithoutTheNullGuardThrows', () => {
    const row = FREE_SEATING.row;
    // @ts-expect-error TS18047: 'row' is possibly 'null' - porównanie wydzielone przed osłoną.
    const vipSeat = row >= 10;
    const hasSeat = row !== null;
    expect(hasSeat && vipSeat).toBe(false);
    // Z `!` kompilator milczy, a wolna widownia "spełnia" próg 0: null >= 0 to true.
    expect(FREE_SEATING.row! >= 0).toBe(true);
  });
});
