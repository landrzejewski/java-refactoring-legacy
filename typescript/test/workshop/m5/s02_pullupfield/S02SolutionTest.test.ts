import { describe, expect, it } from 'vitest';

import type { Ticket } from '../../../../src/workshop/m5/s02_pullupfield/step3/Ticket.js';
import { VipTicket } from '../../../../src/workshop/m5/s02_pullupfield/step3/VipTicket.js';
import { declaresMember, fieldTypeOf, modifiersOf, sourceOf } from '../reflection.js';

// Java czyta pola refleksją (getDeclaredField, Modifier). Pola prywatne ES (#seat) są
// niewidoczne w czasie działania, więc deklaracje i modyfikatory czytamy ze źródła sceny.
const source = (step: string, type: string) => sourceOf('s02_pullupfield', step, `${type}.ts`);

/** Pull Up Field: jedno pole prywatne readonly w bazie, a pola o innym znaczeniu zostają na miejscu. */
describe('S02SolutionTest', () => {
  it('beforePullUpAllSeatFieldsHaveSameNameTypeAndLifecycle', () => {
    for (const type of ['StandardTicket', 'StudentTicket', 'VipTicket']) {
      const file = source('step2', type);
      expect(fieldTypeOf(file, type, '#seat')).toBe('string');
      expect(modifiersOf(file, type, '#seat')).toContain('readonly');
    }
    // przed krokiem 2: inny cykl życia (setter)
    expect(modifiersOf(source('step1', 'StandardTicket'), 'StandardTicket', '#seat')).not.toContain('readonly');
  });

  it('solutionDeclaresSeatOnceAsPrivateFinal', () => {
    // Nazwa z "#" to pole prywatne w czasie działania (odpowiednik private), readonly - odpowiednik final.
    expect(modifiersOf(source('step3', 'Ticket'), 'Ticket', '#seat')).toContain('readonly');
    expect(declaresMember(source('step3', 'VipTicket'), 'VipTicket', '#seat')).toBe(false);
    expect(fieldTypeOf(source('step3', 'StudentTicket'), 'StudentTicket', '#studentId')).toBe('string | null');
  });

  it('solutionReadsSeatThroughBaseType', () => {
    const ticket: Ticket = new VipTicket('k12');
    expect(ticket.seat()).toBe('K12');
  });
});
