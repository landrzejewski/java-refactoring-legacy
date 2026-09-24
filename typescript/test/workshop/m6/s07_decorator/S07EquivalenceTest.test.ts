import { describe } from 'vitest';

import { TicketOrder } from '../../../../src/workshop/m6/s07_decorator/TicketOrder.js';
import * as start from '../../../../src/workshop/m6/s07_decorator/start/TicketAssembler.js';
import * as step1 from '../../../../src/workshop/m6/s07_decorator/step1/TicketAssembler.js';
import * as step2 from '../../../../src/workshop/m6/s07_decorator/step2/TicketAssembler.js';
import * as step3 from '../../../../src/workshop/m6/s07_decorator/step3/TicketAssembler.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { Scene } from '../../support/scene.js';

interface Priced {
  price(): Money;
  description(): string;
}

function show(t: Priced): string {
  return `${t.description()} = ${t.price().toString()}`;
}

function order(title: string, format: string, base: string,
  vip: boolean, ownGlasses: boolean, insurance: boolean): TicketOrder {
  return new TicketOrder(title, format, Money.of(base), vip, ownGlasses, insurance);
}

/** Cena i opis (z kolejnością dodatków) takie same w każdym kroku. */
describe('S07EquivalenceTest', () => {
  describe('everyStepPricesEmbellishmentsTheSame', () => {
    Scene.variants<TicketOrder, string>()
      .variant('start', (o) => show(new start.TicketAssembler().assemble(o)))
      .variant('step1', (o) => show(new step1.TicketAssembler().assemble(o)))
      .variant('step2', (o) => show(new step2.TicketAssembler().assemble(o)))
      .variant('step3', (o) => show(new step3.TicketAssembler().assemble(o)))
      .expect('bez dodatków', order('Amator', '2D', '25.00', false, false, false), 'Amator 2D = 25.00')
      .expect('3D z okularami kina', order('Kraina Lodu', '3D', '32.00', false, false, false),
        'Kraina Lodu 3D +okulary 3D = 35.00')
      .expect('3D z własnymi okularami', order('Kraina Lodu', '3D', '32.00', false, true, false),
        'Kraina Lodu 3D = 32.00')
      .expect('wszystkie dodatki', order('Kraina Lodu', '3D', '32.00', true, false, true),
        'Kraina Lodu 3D +VIP +okulary 3D +ubezpieczenie = 49.00')
      .expect('IMAX VIP z ubezpieczeniem', order('Diuna', 'IMAX', '40.00', true, false, true),
        'Diuna IMAX +VIP +ubezpieczenie = 54.00')
      .tests();
  });
});
