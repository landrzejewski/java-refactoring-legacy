import { describe } from 'vitest';

import { BookingRequest } from '../../../../src/workshop/m8/s01_branchbyabstraction/BookingRequest.js';
import * as start from '../../../../src/workshop/m8/s01_branchbyabstraction/start/BookingService.js';
import * as step1 from '../../../../src/workshop/m8/s01_branchbyabstraction/step1/BookingService.js';
import * as step2 from '../../../../src/workshop/m8/s01_branchbyabstraction/step2/BookingService.js';
import * as step3 from '../../../../src/workshop/m8/s01_branchbyabstraction/step3/BookingService.js';
import { PricingMode } from '../../../../src/workshop/m8/s01_branchbyabstraction/step3/PricingMode.js';
import * as step4 from '../../../../src/workshop/m8/s01_branchbyabstraction/step4/BookingService.js';
import { Scene } from '../../support/scene.js';
import { AMATOR, AMATOR_RANO, DIUNA, GROUP_SEATS, KRAINA_LODU } from './S01Fixtures.js';

/** Test równoważności: każdy krok (i obie gałęzie przełącznika w kroku 3) potwierdza tak samo. */
describe('S01EquivalenceTest', () => {
  describe('everyStepConfirmsTheSameWay', () => {
    const startService = new start.BookingService();
    const step1Service = new step1.BookingService();
    const step2Service = new step2.BookingService();
    const step3Legacy = new step3.BookingService(PricingMode.LEGACY);
    const step3Modern = new step3.BookingService(PricingMode.MODERN);
    const step4Service = new step4.BookingService();
    Scene.variants<BookingRequest, string>()
      .variant('start', (r) => startService.confirm(r))
      .variant('step1', (r) => step1Service.confirm(r))
      .variant('step2', (r) => step2Service.confirm(r))
      .variant('step3 LEGACY', (r) => step3Legacy.confirm(r))
      .variant('step3 MODERN', (r) => step3Modern.confirm(r))
      .variant('step4', (r) => step4Service.confirm(r))
      .expect('IMAX online, student na miejscu VIP',
        new BookingRequest(DIUNA, ['A5', 'A10'], ['N', 'S'], true, false),
        'Diuna: A5,A10 - do zaplaty 84.00')
      .expect('3D rano w kasie, dziecko i normalny, okulary z wypożyczalni',
        new BookingRequest(KRAINA_LODU, ['B1', 'B2'], ['C', 'N'], false, false),
        'Kraina Lodu: B1,B2 - do zaplaty 47.20')
      .expect('3D rano online, własne okulary',
        new BookingRequest(KRAINA_LODU, ['B1', 'B2'], ['C', 'N'], true, true),
        'Kraina Lodu: B1,B2 - do zaplaty 45.20')
      .expect('grupa 10 biletów 2D online',
        new BookingRequest(AMATOR, GROUP_SEATS, Array<string>(10).fill('N'), true, false),
        'Amator: A1,B1,C1,D1,E1,F1,G1,H1,I1,J1 - do zaplaty 245.00')
      .expect('senior rano na miejscu VIP',
        new BookingRequest(AMATOR_RANO, ['C12'], ['E'], false, false),
        'Amator: C12 - do zaplaty 22.50')
      .tests();
  });
});
