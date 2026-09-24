import { describe } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import * as startCalculator from '../../../../src/workshop/m5/s13_sealed/start/PriceCalculator.js';
import * as startSenior from '../../../../src/workshop/m5/s13_sealed/start/SeniorTicket.js';
import * as startStandard from '../../../../src/workshop/m5/s13_sealed/start/StandardTicket.js';
import * as startStudent from '../../../../src/workshop/m5/s13_sealed/start/StudentTicket.js';
import type { Ticket as StartTicket } from '../../../../src/workshop/m5/s13_sealed/start/Ticket.js';
import * as step1Calculator from '../../../../src/workshop/m5/s13_sealed/step1/PriceCalculator.js';
import * as step1Senior from '../../../../src/workshop/m5/s13_sealed/step1/SeniorTicket.js';
import * as step1Standard from '../../../../src/workshop/m5/s13_sealed/step1/StandardTicket.js';
import * as step1Student from '../../../../src/workshop/m5/s13_sealed/step1/StudentTicket.js';
import type { Ticket as Step1Ticket } from '../../../../src/workshop/m5/s13_sealed/step1/Ticket.js';
import * as step2Calculator from '../../../../src/workshop/m5/s13_sealed/step2/PriceCalculator.js';
import * as step2Senior from '../../../../src/workshop/m5/s13_sealed/step2/SeniorTicket.js';
import * as step2Standard from '../../../../src/workshop/m5/s13_sealed/step2/StandardTicket.js';
import * as step2Student from '../../../../src/workshop/m5/s13_sealed/step2/StudentTicket.js';
import type { Ticket as Step2Ticket } from '../../../../src/workshop/m5/s13_sealed/step2/Ticket.js';
import * as step3Calculator from '../../../../src/workshop/m5/s13_sealed/step3/PriceCalculator.js';
import * as step3Senior from '../../../../src/workshop/m5/s13_sealed/step3/SeniorTicket.js';
import * as step3Standard from '../../../../src/workshop/m5/s13_sealed/step3/StandardTicket.js';
import * as step3Student from '../../../../src/workshop/m5/s13_sealed/step3/StudentTicket.js';
import type { Ticket as Step3Ticket } from '../../../../src/workshop/m5/s13_sealed/step3/Ticket.js';
import { Scene } from '../../support/scene.js';

interface Sale {
  readonly kind: string;
  readonly basePrice: string;
}

type Variant<T> = new (basePrice: Money) => T;

function ticketFor<T>(sale: Sale, standard: Variant<T>, student: Variant<T>, senior: Variant<T>): T {
  const base = Money.of(sale.basePrice);
  switch (sale.kind) {
    case 'STUDENT': return new student(base);
    case 'SENIOR': return new senior(base);
    default: return new standard(base);
  }
}

/** Test równoważności: dla znanych typów biletów cena jest identyczna w start i każdym kroku. */
describe('S13EquivalenceTest', () => {
  describe('everyStepPricesKnownTicketsTheSameWay', () => {
    Scene.variants<Sale, string>()
      .variant('start', (s) => new startCalculator.PriceCalculator().price(ticketFor<StartTicket>(
        s, startStandard.StandardTicket, startStudent.StudentTicket, startSenior.SeniorTicket)).toString())
      .variant('step1', (s) => new step1Calculator.PriceCalculator().price(
        ticketFor<Step1Ticket>(
          s, step1Standard.StandardTicket, step1Student.StudentTicket, step1Senior.SeniorTicket)).toString())
      .variant('step2', (s) => new step2Calculator.PriceCalculator().price(
        ticketFor<Step2Ticket>(
          s, step2Standard.StandardTicket, step2Student.StudentTicket, step2Senior.SeniorTicket)).toString())
      .variant('step3', (s) => new step3Calculator.PriceCalculator().price(
        ticketFor<Step3Ticket>(
          s, step3Standard.StandardTicket, step3Student.StudentTicket, step3Senior.SeniorTicket)).toString())
      .expect('normalny 2D', { kind: 'NORMAL', basePrice: '25.00' }, '25.00')
      .expect('studencki 3D', { kind: 'STUDENT', basePrice: '32.00' }, '24.00')
      .expect('senior IMAX', { kind: 'SENIOR', basePrice: '40.00' }, '28.00')
      .tests();
  });
});
