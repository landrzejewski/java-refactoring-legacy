import { describe } from 'vitest';

import { IllegalStateError } from '../../../../src/shared/errors.js';
import type { LoyaltyProgram } from '../../../../src/workshop/m6/s09_observer/LoyaltyProgram.js';
import type { Mailer } from '../../../../src/workshop/m6/s09_observer/Mailer.js';
import { Payment } from '../../../../src/workshop/m6/s09_observer/Payment.js';
import type { SmsGateway } from '../../../../src/workshop/m6/s09_observer/SmsGateway.js';
import * as start from '../../../../src/workshop/m6/s09_observer/start/PaymentServices.js';
import * as step1 from '../../../../src/workshop/m6/s09_observer/step1/PaymentServices.js';
import * as step2 from '../../../../src/workshop/m6/s09_observer/step2/PaymentServices.js';
import * as step3 from '../../../../src/workshop/m6/s09_observer/step3/PaymentServices.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { Scene } from '../../support/scene.js';

/** failing: "mail", "sms", "loyalty" albo "" - który odbiorca rzuca wyjątek. */
interface Case {
  readonly payment: Payment;
  readonly failing: string;
}

interface Ports {
  readonly mailer: Mailer;
  readonly sms: SmsGateway;
  readonly loyalty: LoyaltyProgram;
}

interface Service {
  confirm(payment: Payment): void;
  paid(): readonly string[];
}

function payment(amount: string): Payment {
  return new Payment('R1', 'anna@kino.pl', '600100200', Money.of(amount));
}

function failIf(c: Case, channel: string): void {
  if (c.failing === channel) {
    throw new IllegalStateError(`${channel} down`);
  }
}

function play(c: Case, factory: (ports: Ports) => Service): string {
  const log: string[] = [];
  const ports: Ports = {
    mailer: {
      send: (to, text) => {
        failIf(c, 'mail');
        log.push(`mail ${to}: ${text}`);
      },
    },
    sms: {
      send: (phone, text) => {
        failIf(c, 'sms');
        log.push(`sms ${phone}: ${text}`);
      },
    },
    loyalty: {
      addPoints: (email, points) => {
        failIf(c, 'loyalty');
        log.push(`points ${email} +${points}`);
      },
    },
  };
  const service = factory(ports);
  try {
    service.confirm(c.payment);
  } catch (error) {
    log.push(`ERROR ${(error as Error).message}`);
  }
  log.push(`paid=[${service.paid().join(', ')}]`);
  return log.join('\n');
}

/** Kolejność powiadomień i polityka błędów (fail-fast) takie same w każdym kroku. */
describe('S09EquivalenceTest', () => {
  describe('everyStepNotifiesTheSameWay', () => {
    Scene.variants<Case, string>()
      .variant('start', (c) => play(c, (p) => start.PaymentServices.standard(p.mailer, p.sms, p.loyalty)))
      .variant('step1', (c) => play(c, (p) => step1.PaymentServices.standard(p.mailer, p.sms, p.loyalty)))
      .variant('step2', (c) => play(c, (p) => step2.PaymentServices.standard(p.mailer, p.sms, p.loyalty)))
      .variant('step3', (c) => play(c, (p) => step3.PaymentServices.standard(p.mailer, p.sms, p.loyalty)))
      .expect('wszyscy odbiorcy w kolejności', { payment: payment('95.50'), failing: '' }, `mail anna@kino.pl: Potwierdzenie platnosci R1: 95.50
sms 600100200: Oplacono R1
points anna@kino.pl +9
paid=[R1]`)
      .expect('wyjątek w SMS: mail wysłany, punkty nie, opłata zapisana', { payment: payment('40.00'), failing: 'sms' }, `mail anna@kino.pl: Potwierdzenie platnosci R1: 40.00
ERROR sms down
paid=[R1]`)
      .expect('wyjątek w mailu przerywa wszystko', { payment: payment('40.00'), failing: 'mail' }, `ERROR mail down
paid=[R1]`)
      .expect('kwota niedodatnia - nikt nie dostaje powiadomienia', { payment: payment('0.00'), failing: '' }, `ERROR amount must be positive
paid=[]`)
      .tests();
  });
});
