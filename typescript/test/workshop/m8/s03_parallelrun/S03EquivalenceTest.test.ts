import { describe } from 'vitest';

import type { TicketQuery } from '../../../../src/workshop/m8/s03_parallelrun/TicketQuery.js';
import * as start from '../../../../src/workshop/m8/s03_parallelrun/start/PriceService.js';
import * as step1 from '../../../../src/workshop/m8/s03_parallelrun/step1/PriceService.js';
import * as step2 from '../../../../src/workshop/m8/s03_parallelrun/step2/PriceService.js';
import * as step3 from '../../../../src/workshop/m8/s03_parallelrun/step3/PriceService.js';
import { MigrationMode } from '../../../../src/workshop/m8/s03_parallelrun/step4/MigrationMode.js';
import * as step4 from '../../../../src/workshop/m8/s03_parallelrun/step4/PriceService.js';
import { VerificationReport } from '../../../../src/workshop/m8/s03_parallelrun/step4/VerificationReport.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { Scene } from '../../support/scene.js';
import {
  EVENING_2D_SENIOR, IMAX_NORMAL, IMAX_STUDENT_VIP, MORNING_2D_STUDENT, MORNING_3D_CHILD, MORNING_3D_NORMAL_VIP,
} from './S03Fixtures.js';

/** Test równoważności: klient dostaje tę samą cenę w każdym kroku i w każdym trybie migracji. */
describe('S03EquivalenceTest', () => {
  describe('customerPaysTheSameInEveryStep', () => {
    const services: Array<[string, { price(query: TicketQuery): Money }]> = [
      ['start', new start.PriceService()],
      ['step1', new step1.PriceService()],
      ['step2', new step2.PriceService()],
      ['step3', new step3.PriceService()],
      ['step4 SHADOW', new step4.PriceService()],
      ['step4 CANDIDATE', new step4.PriceService(MigrationMode.CANDIDATE, new VerificationReport())],
      ['step4 LEGACY', new step4.PriceService(MigrationMode.LEGACY, new VerificationReport())],
    ];
    const scene = Scene.variants<TicketQuery, Money>();
    for (const [name, service] of services) {
      scene.variant(name, (query) => service.price(query));
    }
    scene
      .expect('IMAX normalny wieczorem', IMAX_NORMAL, Money.of('40.00'))
      .expect('IMAX student na VIP', IMAX_STUDENT_VIP, Money.of('40.00'))
      .expect('3D dziecko rano', MORNING_3D_CHILD, Money.of('17.20'))
      .expect('3D normalny rano na VIP', MORNING_3D_NORMAL_VIP, Money.of('40.00'))
      .expect('2D senior wieczorem', EVENING_2D_SENIOR, Money.of('17.50'))
      .expect('2D student rano', MORNING_2D_STUDENT, Money.of('13.75'))
      .tests();
  });
});
