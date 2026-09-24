import { describe, expect, it } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import { StandardTicket as step2StandardTicket } from '../../../../src/workshop/m5/s01_pullupmethod/step2/StandardTicket.js';
import { StudentTicket as step2StudentTicket } from '../../../../src/workshop/m5/s01_pullupmethod/step2/StudentTicket.js';
import { Ticket as step2Ticket } from '../../../../src/workshop/m5/s01_pullupmethod/step2/Ticket.js';
import { VipTicket as step2VipTicket } from '../../../../src/workshop/m5/s01_pullupmethod/step2/VipTicket.js';
import { StandardTicket as step3StandardTicket } from '../../../../src/workshop/m5/s01_pullupmethod/step3/StandardTicket.js';
import { StudentTicket as step3StudentTicket } from '../../../../src/workshop/m5/s01_pullupmethod/step3/StudentTicket.js';
import { Ticket as step3Ticket } from '../../../../src/workshop/m5/s01_pullupmethod/step3/Ticket.js';
import { VipTicket as step3VipTicket } from '../../../../src/workshop/m5/s01_pullupmethod/step3/VipTicket.js';
import { declares, modifiersOf, sourceOf } from '../reflection.js';

/** Po Pull Up zmienia się typ deklarujący metody - to widzi prototyp i każdy stary kod, który go czyta. */
describe('S01SolutionTest', () => {
  it('beforeLastStepEverySubclassDeclaresLabel', () => {
    expect(declares(step2Ticket, 'label')).toBe(false);
    expect(declares(step2StandardTicket, 'label')).toBe(true);
    expect(declares(step2StudentTicket, 'label')).toBe(true);
    expect(declares(step2VipTicket, 'label')).toBe(true);
  });

  it('solutionDeclaresLabelOnceAsFinalAndPriceAsAbstract', () => {
    // Java sprawdza Modifier.isFinal(label) i Modifier.isAbstract(price). TS nie ma metod
    // `final`, więc sprawdzamy sens: label() jest na prototypie Ticket, a żadna podklasa
    // go nie deklaruje. `abstract` znika po kompilacji - czytamy go ze źródła.
    const source = sourceOf('s01_pullupmethod', 'step3', 'Ticket.ts');
    expect(declares(step3Ticket, 'label')).toBe(true);
    expect(modifiersOf(source, 'Ticket', 'price')).toContain('abstract');
    expect(declares(step3Ticket, 'price')).toBe(false);
    for (const subclass of [step3StandardTicket, step3StudentTicket, step3VipTicket]) {
      expect(declares(subclass, 'label')).toBe(false);
      expect(subclass.prototype.label).toBe(step3Ticket.prototype.label);
    }
  });

  it('solutionIsUsableThroughBaseType', () => {
    const ticket: step3Ticket = new step3StudentTicket('Amator', Money.of('25.00'));
    expect(ticket.label()).toBe('Amator: 18.75');
  });
});
