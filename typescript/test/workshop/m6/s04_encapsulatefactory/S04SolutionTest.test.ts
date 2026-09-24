import { existsSync, readFileSync } from 'node:fs';

import { describe, expect, it } from 'vitest';

import * as tickets from '../../../../src/workshop/m6/s04_encapsulatefactory/step3/tickets/Tickets.js';
import { workshopDir } from '../../support/paths.js';

const STEP3 = (file: string) => workshopDir('m6', 's04_encapsulatefactory', 'step3', 'tickets', file);

/** Po kroku 3 publiczne (eksportowane) są tylko Ticket i fabryka Tickets. */
describe('S04SolutionTest', () => {
  it('solutionHidesConcreteTicketClassesBehindTheFactory', () => {
    // klasy konkretne nie są eksportowane - nie ma ich osobnych modułów ani eksportu z fabryki
    expect(existsSync(STEP3('StandardTicket.ts'))).toBe(false);
    expect(existsSync(STEP3('VipTicket.ts'))).toBe(false);
    expect(Object.keys(tickets)).toEqual(['Tickets']);
    expect(readFileSync(STEP3('Tickets.ts'), 'utf8')).toMatch(/^class StandardTicket\b/m);
    expect(readFileSync(STEP3('Tickets.ts'), 'utf8')).toMatch(/^class VipTicket\b/m);
    // publiczny kontrakt zostaje
    expect(readFileSync(STEP3('Ticket.ts'), 'utf8')).toMatch(/^export interface Ticket\b/m);
  });
});
