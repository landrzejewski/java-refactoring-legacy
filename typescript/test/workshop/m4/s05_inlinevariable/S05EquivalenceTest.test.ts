import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m4/s05_inlinevariable/start/TicketIssuer.js';
import * as step1 from '../../../../src/workshop/m4/s05_inlinevariable/step1/TicketIssuer.js';
import * as step2 from '../../../../src/workshop/m4/s05_inlinevariable/step2/TicketIssuer.js';
import * as step3 from '../../../../src/workshop/m4/s05_inlinevariable/step3/TicketIssuer.js';
import { Ticket } from '../../../../src/workshop/m4/s05_inlinevariable/Ticket.js';
import { type Clock, LocalDateTime } from '../../../../src/workshop/shared/time.js';
import { Scene } from '../../support/scene.js';
import { T0, TickingClock } from './TickingClock.js';

interface Request {
  readonly screeningCode: string;
  readonly format: number;
}

type Issue = (screeningCode: string, format: number) => Ticket;

function issuing(issuerFactory: (clock: Clock) => Issue): (requests: readonly Request[]) => Ticket[] {
  return (requests) => {
    const issue = issuerFactory(new TickingClock(T0));
    return requests.map((request) => issue(request.screeningCode, request.format));
  };
}

const plusSeconds = (seconds: number): LocalDateTime => LocalDateTime.ofEpochMillis(T0.toEpochMillis() + seconds * 1000);

/** Test równoważności: te same bilety (numery, etykiety, czasy) po każdym kroku. */
describe('S05EquivalenceTest', () => {
  describe('everyStepIssuesTheSameTickets', () => {
    Scene.variants<readonly Request[], Ticket[]>()
      .variant('start', issuing((clock) => {
        const issuer = new start.TicketIssuer(clock);
        return (code, format) => issuer.issue(code, format);
      }))
      .variant('step1', issuing((clock) => {
        const issuer = new step1.TicketIssuer(clock);
        return (code, format) => issuer.issue(code, format);
      }))
      .variant('step2', issuing((clock) => {
        const issuer = new step2.TicketIssuer(clock);
        return (code, format) => issuer.issue(code, format);
      }))
      .variant('step3', issuing((clock) => {
        const issuer = new step3.TicketIssuer(clock);
        return (code, format) => issuer.issue(code, format);
      }))
      .expect('dwa bilety: kolejne numery, jeden odczyt zegara na bilet',
        [{ screeningCode: 'D1', format: 3 }, { screeningCode: 'K2', format: 2 }],
        [new Ticket('D1-1', 'Bilet D1-1, cena 40.00, oplata 2.00',
          T0, plusSeconds(15 * 60)),
        new Ticket('K2-2', 'Bilet K2-2, cena 32.00, oplata 2.00',
          plusSeconds(1), plusSeconds(1 + 15 * 60))])
      .expect('2D', [{ screeningCode: 'A3', format: 1 }],
        [new Ticket('A3-1', 'Bilet A3-1, cena 25.00, oplata 2.00',
          T0, plusSeconds(15 * 60))])
      .tests();
  });
});
