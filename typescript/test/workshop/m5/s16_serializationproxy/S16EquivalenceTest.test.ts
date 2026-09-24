import { describe } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import { load, save } from '../../../../src/workshop/m5/s16_serializationproxy/SessionStore.js';
import * as startPricing from '../../../../src/workshop/m5/s16_serializationproxy/start/TicketPricing.js';
import * as startTicket from '../../../../src/workshop/m5/s16_serializationproxy/start/StudentTicket.js';
import * as step1Pricing from '../../../../src/workshop/m5/s16_serializationproxy/step1/TicketPricing.js';
import * as step1Ticket from '../../../../src/workshop/m5/s16_serializationproxy/step1/StudentTicket.js';
import * as step2Pricing from '../../../../src/workshop/m5/s16_serializationproxy/step2/TicketPricing.js';
import * as step2Ticket from '../../../../src/workshop/m5/s16_serializationproxy/step2/StudentTicket.js';
import * as step3Pricing from '../../../../src/workshop/m5/s16_serializationproxy/step3/TicketPricing.js';
import * as step3Ticket from '../../../../src/workshop/m5/s16_serializationproxy/step3/StudentTicket.js';
import { Scene } from '../../support/scene.js';

/** Zapis i odczyt w tej samej wersji klasy (odpowiednik roundTrip przez ObjectOutputStream). */
function roundTrip<T extends object>(value: T, type: { readonly name: string; readonly prototype: T }): T {
  return load(save(value), type) as T;
}

/** Test równoważności: opis biletu po zapisie i odczycie w tej samej wersji oraz cena - bez zmian. */
describe('S16EquivalenceTest', () => {
  describe('everyStepRoundTripsTicketTheSameWay', () => {
    Scene.variants<string, string>()
      .variant('start', (id) => roundTrip(new startTicket.StudentTicket('Amator', 'F3', id), startTicket.StudentTicket).describe())
      .variant('step1', (id) => roundTrip(new step1Ticket.StudentTicket('Amator', 'F3', id), step1Ticket.StudentTicket).describe())
      .variant('step2', (id) => roundTrip(new step2Ticket.StudentTicket('Amator', 'F3', id), step2Ticket.StudentTicket).describe())
      .variant('step3', (id) => roundTrip(new step3Ticket.StudentTicket('Amator', 'F3', id), step3Ticket.StudentTicket).describe())
      .expect('bilet studencki', 'S-123', 'Amator F3 (legitymacja S-123)')
      .tests();
  });

  describe('everyStepPricesStudentTicketTheSameWay', () => {
    Scene.variants<string, string>()
      .variant('start', (p) => new startPricing.TicketPricing().studentPrice(Money.of(p)).toString())
      .variant('step1', (p) => new step1Pricing.TicketPricing().studentPrice(Money.of(p)).toString())
      .variant('step2', (p) => new step2Pricing.TicketPricing().studentPrice(Money.of(p)).toString())
      .variant('step3', (p) => new step3Pricing.TicketPricing().studentPrice(Money.of(p)).toString())
      .expect('studencki 3D', '32.00', '24.00')
      .tests();
  });
});
