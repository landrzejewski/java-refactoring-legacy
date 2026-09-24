import { describe } from 'vitest';

import type { Ticket } from '../../../../src/workshop/m4/s06_inlinemethod/Ticket.js';
import * as startOnline from '../../../../src/workshop/m4/s06_inlinemethod/start/OnlineTicketPricing.js';
import * as start from '../../../../src/workshop/m4/s06_inlinemethod/start/TicketPricing.js';
import * as step1Online from '../../../../src/workshop/m4/s06_inlinemethod/step1/OnlineTicketPricing.js';
import * as step1 from '../../../../src/workshop/m4/s06_inlinemethod/step1/TicketPricing.js';
import * as step2Online from '../../../../src/workshop/m4/s06_inlinemethod/step2/OnlineTicketPricing.js';
import * as step2 from '../../../../src/workshop/m4/s06_inlinemethod/step2/TicketPricing.js';
import { Scene } from '../../support/scene.js';
import { IMAX_EVENING, MORNING_3D, NOON_2D } from './tickets.js';

/** Test równoważności: kasa i internet - te same kwoty po każdym kroku. Podklasa jest częścią testu! */
describe('S06EquivalenceTest', () => {
  describe('boxOfficeTotalsStayTheSame', () => {
    Scene.variants<Ticket, string>()
      .variant('start', (t) => new start.TicketPricing().total(t).toFixed(2))
      .variant('step1', (t) => new step1.TicketPricing().total(t).toFixed(2))
      .variant('step2', (t) => new step2.TicketPricing().total(t).toFixed(2))
      .expect('kasa: IMAX wieczorem', IMAX_EVENING, '40.00')
      .expect('kasa: 3D rano', MORNING_3D, '27.00')
      .expect('kasa: 2D 12:00', NOON_2D, '25.00')
      .tests();
  });

  describe('onlineTotalsStayTheSame', () => {
    Scene.variants<Ticket, string>()
      .variant('start', (t) => new startOnline.OnlineTicketPricing().total(t).toFixed(2))
      .variant('step1', (t) => new step1Online.OnlineTicketPricing().total(t).toFixed(2))
      .variant('step2', (t) => new step2Online.OnlineTicketPricing().total(t).toFixed(2))
      .expect('online: IMAX wieczorem', IMAX_EVENING, '42.00')
      .expect('online: 3D rano', MORNING_3D, '29.00')
      .expect('online: 2D 12:00', NOON_2D, '27.00')
      .tests();
  });
});
