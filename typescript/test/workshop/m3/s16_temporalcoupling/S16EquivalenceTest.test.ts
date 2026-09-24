import { describe, expect, it } from 'vitest';

import { NullPointerError } from '../../../../src/shared/errors.js';
import { LocalDateTime } from '../../../../src/workshop/shared/time.js';
import { Screening } from '../../../../src/workshop/m3/s16_temporalcoupling/Screening.js';
import * as start from '../../../../src/workshop/m3/s16_temporalcoupling/start/TicketDesk.js';
import * as step1 from '../../../../src/workshop/m3/s16_temporalcoupling/step1/TicketDesk.js';
import * as step2 from '../../../../src/workshop/m3/s16_temporalcoupling/step2/TicketDesk.js';
import { TicketRequest } from '../../../../src/workshop/m3/s16_temporalcoupling/step2/TicketRequest.js';
import { Scene } from '../../support/scene.js';

interface Case {
  readonly screening: Screening;
  readonly seat: number;
  readonly buyer: string;
}

const DUNE = new Screening('Diuna', 'IMAX', LocalDateTime.of(2026, 10, 2, 20, 0));

/** Wydruk jest identyczny; znika tylko możliwość złego wywołania. */
describe('S16EquivalenceTest', () => {
  describe('everyStepPrintsTheSameTicket', () => {
    Scene.variants<Case, string>()
      .variant('start', (c) => new start.TicketDesk().issue(c.screening, c.seat, c.buyer))
      .variant('step1', (c) => new step1.TicketDesk().issue(c.screening, c.seat, c.buyer))
      .variant('step2', (c) => new step2.TicketDesk().issue(c.screening, c.seat, c.buyer))
      .expect('Diuna IMAX', { screening: DUNE, seat: 14, buyer: 'Anna@Kino.pl' },
        'BILET Diuna (IMAX) 2026-10-02T20:00, miejsce 14, dla anna@kino.pl')
      .expect('Kraina Lodu rano',
        { screening: new Screening('Kraina Lodu', '3D', LocalDateTime.of(2026, 10, 3, 10, 0)), seat: 3, buyer: 'jan@kino.pl' },
        'BILET Kraina Lodu (3D) 2026-10-03T10:00, miejsce 3, dla jan@kino.pl')
      .tests();
  });

  it('step2RejectsIncompleteRequestAtCreation', () => {
    // null spoza systemu typów (np. z JSON-a) - rzutowanie tylko w teście
    expect(() => new TicketRequest(DUNE, 1, null as unknown as string)).toThrow(NullPointerError);
  });
});
