import { describe, expect, it } from 'vitest';

import { UnsupportedOperationError } from '../../../../src/shared/errors.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { StandardTicket as Step2StandardTicket } from '../../../../src/workshop/m5/s03_pushdown/step2/StandardTicket.js';
import { StudentTicket as Step2StudentTicket } from '../../../../src/workshop/m5/s03_pushdown/step2/StudentTicket.js';
import { Ticket as Step2Ticket } from '../../../../src/workshop/m5/s03_pushdown/step2/Ticket.js';
import { StandardTicket as Step3StandardTicket } from '../../../../src/workshop/m5/s03_pushdown/step3/StandardTicket.js';
import { Ticket as Step3Ticket } from '../../../../src/workshop/m5/s03_pushdown/step3/Ticket.js';
import { declaresMember, declaringClassOf, sourceOf } from '../reflection.js';

/**
 * Push Down zawęża kontrakt bazy - i łamie starych klientów, którzy wołali metodę przez nadklasę.
 * W JS wywołanie metody szuka jej w łańcuchu prototypów w górę (jak JVM w nadklasach), nigdy w dół.
 */
describe('S03SolutionTest', () => {
  it('beforePushDownStudentTicketBreaksBaseContract', () => {
    const ticket: Step2Ticket = new Step2StudentTicket(Money.of('25.00'));
    expect(() => ticket.upgradeToVip()).toThrow(UnsupportedOperationError);
  });

  it('beforePushDownSubclassFindsMethodInSuperclass', () => {
    expect(declaringClassOf(Step2StandardTicket, 'upgradeToVip')).toBe(Step2Ticket);
  });

  it('afterPushDownBaseTypeNoLongerHasTheMethod', () => {
    expect(declaringClassOf(Step3Ticket, 'upgradeToVip')).toBeUndefined();
    expect(declaringClassOf(Step3StandardTicket, 'upgradeToVip')).toBe(Step3StandardTicket);
    expect(declaresMember(sourceOf('s03_pushdown', 'step3', 'Ticket.ts'), 'Ticket', '#vipUpgraded')).toBe(false);
  });
});
