import { describe, expect, it } from 'vitest';

import { StudentTicket as StartStudentTicket } from '../../../../src/workshop/m5/s10_fieldhiding/start/StudentTicket.js';
import { StudentTicket as Step1StudentTicket } from '../../../../src/workshop/m5/s10_fieldhiding/step1/StudentTicket.js';
import { StudentTicket as Step2StudentTicket } from '../../../../src/workshop/m5/s10_fieldhiding/step2/StudentTicket.js';
import type { Ticket as Step2Ticket } from '../../../../src/workshop/m5/s10_fieldhiding/step2/Ticket.js';
import { declaresMember, modifiersOf, sourceOf } from '../reflection.js';

const source = (step: string, type: string) => sourceOf('s10_fieldhiding', step, `${type}.ts`);

/** Pola prywatne ES i metody static są wiązane z klasą, w której stoi kod - nie z obiektem. */
describe('S10SolutionTest', () => {
  it('startHidesFieldSoOneObjectHasTwoSlots', () => {
    // Oba typy deklarują własne #type - to dwa sloty w jednym obiekcie.
    expect(declaresMember(source('start', 'Ticket'), 'Ticket', '#type')).toBe(true);
    expect(declaresMember(source('start', 'StudentTicket'), 'StudentTicket', '#type')).toBe(true);
    const student = new StartStudentTicket();
    // slot StudentTicket (przez nadpisany akcesor)
    expect(student.type()).toBe('STUDENT');
    // pułapka: kod Ticket czyta slot Ticket - drugi slot w tym samym obiekcie
    expect(student.label()).toBe('BILET: NORMAL');
  });

  it('startStaticMethodIsHiddenNotOverridden', () => {
    expect(modifiersOf(source('start', 'StudentTicket'), 'StudentTicket', 'category')).toContain('static');
    expect(StartStudentTicket.category()).toBe('BILET ULGOWY');
    // pułapka: label() widzi pole i static bazy
    expect(new StartStudentTicket().label()).toBe('BILET: NORMAL');
  });

  it('step1FixesFieldButStaticIsStillHidden', () => {
    expect(new Step1StudentTicket().label()).toBe('BILET: STUDENT');
    expect(declaresMember(source('step1', 'StudentTicket'), 'StudentTicket', '#type')).toBe(false);
  });

  it('solutionDispatchesOnObjectRegardlessOfReferenceType', () => {
    const ticket: Step2Ticket = new Step2StudentTicket();
    expect(ticket.label()).toBe('BILET ULGOWY: STUDENT');
    expect(ticket.category()).toBe('BILET ULGOWY');
  });
});
