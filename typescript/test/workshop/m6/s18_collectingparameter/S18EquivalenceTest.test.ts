import { describe } from 'vitest';

import { LocalDateTime } from '../../../../src/workshop/shared/time.js';
import { ReservationDraft } from '../../../../src/workshop/m6/s18_collectingparameter/ReservationDraft.js';
import * as start from '../../../../src/workshop/m6/s18_collectingparameter/start/ReservationValidator.js';
import * as step1 from '../../../../src/workshop/m6/s18_collectingparameter/step1/ReservationValidator.js';
import * as step2 from '../../../../src/workshop/m6/s18_collectingparameter/step2/ReservationValidator.js';
import * as step3 from '../../../../src/workshop/m6/s18_collectingparameter/step3/ReservationValidator.js';
import { Scene } from '../../support/scene.js';

const SHOW = LocalDateTime.of(2026, 10, 2, 18, 0);
const BEFORE = SHOW.minusHours(2);

/** Te same ostrzeżenia, w tej samej kolejności i z tym samym separatorem. */
describe('S18EquivalenceTest', () => {
  describe('everyStepCollectsTheSameWarnings', () => {
    Scene.variants<ReservationDraft, string>()
      .variant('start', (d) => new start.ReservationValidator().validate(d))
      .variant('step1', (d) => new step1.ReservationValidator().validate(d))
      .variant('step2', (d) => new step2.ReservationValidator().validate(d))
      .variant('step3', (d) => new step3.ReservationValidator().validate(d))
      .expect('poprawna', new ReservationDraft('anna@kino.pl', ['A1', 'A2'], SHOW, BEFORE), 'OK')
      .expect('brak e-maila i miejsc', new ReservationDraft(' ', [], SHOW, BEFORE), 'brak e-maila; brak miejsc')
      .expect('null e-mail', new ReservationDraft(null, ['A1'], SHOW, BEFORE), 'brak e-maila')
      .expect('zły e-mail, duplikaty zgłoszone raz, po starcie',
        new ReservationDraft('jan.kino.pl', ['A1', 'A2', 'A1', 'A1', 'A2'], SHOW, SHOW),
        'niepoprawny e-mail: jan.kino.pl; miejsce A1 zdublowane; miejsce A2 zdublowane; '
          + 'seans juz sie rozpoczal')
      .expect('grupa 10+', new ReservationDraft('jan@kino.pl',
        ['B1', 'B2', 'B3', 'B4', 'B5', 'B6', 'B7', 'B8', 'B9', 'B10'], SHOW, BEFORE),
      'grupa 10+: zastosuj rabat grupowy')
      .tests();
  });
});
