import { describe, expect, it } from 'vitest';

import type { Ticket } from '../../../../src/workshop/m8/s05_boyscout/Ticket.js';
import * as start from '../../../../src/workshop/m8/s05_boyscout/start/TicketPrinter.js';
import * as step1 from '../../../../src/workshop/m8/s05_boyscout/step1/TicketPrinter.js';
import * as step2 from '../../../../src/workshop/m8/s05_boyscout/step2/TicketPrinter.js';
import { MIXED_CASE_EMAIL, NO_PHONE, REGULAR, ROWS_9_AND_10 } from './S05Fixtures.js';

const cases = new Map<string, Ticket>([
  ['zwykły bilet', REGULAR],
  ['miejsca w rzędach 9 i 10', ROWS_9_AND_10],
  ['brak telefonu', NO_PHONE],
  ['e-mail z wielkimi literami', MIXED_CASE_EMAIL],
]);

/** Test różnicowy demaskuje "sprzątanie", które zmieniło zachowanie. */
describe('S05SolutionTest', () => {
  it('abusiveCleanupChangesBehaviourInThreeCases', () => {
    const before = new start.TicketPrinter();
    const abuse = new step1.TicketPrinter();
    const changed: string[] = [];
    cases.forEach((ticket, name) => {
      if (before.print(ticket) !== abuse.print(ticket)) {
        changed.push(name);
      }
    });
    expect(changed).toEqual(['miejsca w rzędach 9 i 10', 'brak telefonu', 'e-mail z wielkimi literami']);
  });

  it('abusiveCleanupSortsSeatsLexicographically', () => {
    const printed = new step1.TicketPrinter().print(ROWS_9_AND_10);
    expect(printed.split('\n').find((line) => line.startsWith('Miejsca'))).toBe('Miejsca: A10, A9');
  });

  it('correctBoyScoutStepChangesNothingObservable', () => {
    const before = new start.TicketPrinter();
    const after = new step2.TicketPrinter();
    cases.forEach((ticket, name) => expect(after.print(ticket), name).toBe(before.print(ticket)));
  });
});
