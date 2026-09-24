import { describe } from 'vitest';

import type { Ticket } from '../../../../src/workshop/m8/s05_boyscout/Ticket.js';
import * as start from '../../../../src/workshop/m8/s05_boyscout/start/TicketPrinter.js';
import * as step2 from '../../../../src/workshop/m8/s05_boyscout/step2/TicketPrinter.js';
import { Scene } from '../../support/scene.js';
import { MIXED_CASE_EMAIL, NO_PHONE, REGULAR, ROWS_9_AND_10 } from './S05Fixtures.js';

/**
 * Test równoważności: start i poprawna poprawa (step2). Krok 1 celowo NIE jest tu wariantem -
 * to nadużycie, które S05SolutionTest demaskuje.
 */
describe('S05EquivalenceTest', () => {
  describe('boyScoutCleanupKeepsTheTicketIdentical', () => {
    Scene.variants<Ticket, string>()
      .variant('start', (t) => new start.TicketPrinter().print(t))
      .variant('step2', (t) => new step2.TicketPrinter().print(t))
      .expect('zwykły bilet', REGULAR, `Film: Amator
Seans: 2026-03-14 18:00
Miejsca: C5
Klient: jan@kino.pl
Tel: 600100200
Do zaplaty: 25.00
`)
      .expect('miejsca w rzędach 9 i 10', ROWS_9_AND_10, `Film: Diuna
Seans: 2026-03-13 20:00
Miejsca: A9, A10
Klient: anna@kino.pl
Tel: 600100300
Do zaplaty: 84.00
`)
      .expect('brak telefonu', NO_PHONE, `Film: Kraina Lodu
Seans: 2026-03-14 10:30
Miejsca: B1, B2
Klient: ola@kino.pl
Tel: -
Do zaplaty: 47.20
`)
      .expect('e-mail z wielkimi literami', MIXED_CASE_EMAIL, `Film: Amator
Seans: 2026-03-14 18:00
Miejsca: D7
Klient: Anna@Kino.pl
Tel: 600100400
Do zaplaty: 25.00
`)
      .tests();
  });
});
