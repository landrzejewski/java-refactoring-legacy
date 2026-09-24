import { describe } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import * as startCart from '../../../../src/workshop/m5/s06_extractinterface/start/Cart.js';
import * as startSnack from '../../../../src/workshop/m5/s06_extractinterface/start/Snack.js';
import * as startTicket from '../../../../src/workshop/m5/s06_extractinterface/start/Ticket.js';
import * as step1Cart from '../../../../src/workshop/m5/s06_extractinterface/step1/Cart.js';
import * as step1Snack from '../../../../src/workshop/m5/s06_extractinterface/step1/Snack.js';
import * as step1Ticket from '../../../../src/workshop/m5/s06_extractinterface/step1/Ticket.js';
import * as step2Cart from '../../../../src/workshop/m5/s06_extractinterface/step2/Cart.js';
import * as step2Snack from '../../../../src/workshop/m5/s06_extractinterface/step2/Snack.js';
import * as step2Ticket from '../../../../src/workshop/m5/s06_extractinterface/step2/Ticket.js';
import * as step3Cart from '../../../../src/workshop/m5/s06_extractinterface/step3/Cart.js';
import * as step3Snack from '../../../../src/workshop/m5/s06_extractinterface/step3/Snack.js';
import * as step3Ticket from '../../../../src/workshop/m5/s06_extractinterface/step3/Ticket.js';
import { Scene } from '../../support/scene.js';

interface Basket {
  readonly tickets: readonly string[];
  readonly snacks: readonly string[];
}

/**
 * Start woła metodę po nazwie: po `jump`/`next` w start może być już API kroku 2 (jedno `add`
 * zamiast `addTicket`/`addSnack` - TS nie ma przeciążeń), a test ma się nadal typować.
 */
function addTo(cart: object, method: 'addTicket' | 'addSnack', item: unknown): void {
  const methods = cart as Record<string, ((item: unknown) => void) | undefined>;
  const add = methods[method] ?? methods['add'];
  add!.call(cart, item);
}

/** Test równoważności: podsumowanie koszyka (suma i VAT 8%/23%) identyczne w start i każdym kroku. */
describe('S06EquivalenceTest', () => {
  describe('everyStepSummarizesTheCartTheSameWay', () => {
    Scene.variants<Basket, string>()
      .variant('start', (b) => {
        const cart = new startCart.Cart();
        b.tickets.forEach((p) => addTo(cart, 'addTicket', new startTicket.Ticket('Diuna', 'H7', Money.of(p))));
        b.snacks.forEach((p) => addTo(cart, 'addSnack', new startSnack.Snack('Popcorn', Money.of(p))));
        return cart.summary();
      })
      .variant('step1', (b) => {
        const cart = new step1Cart.Cart();
        b.tickets.forEach((p) => cart.addTicket(new step1Ticket.Ticket('Diuna', 'H7', Money.of(p))));
        b.snacks.forEach((p) => cart.addSnack(new step1Snack.Snack('Popcorn', Money.of(p))));
        return cart.summary();
      })
      .variant('step2', (b) => {
        const cart = new step2Cart.Cart();
        b.tickets.forEach((p) => cart.add(new step2Ticket.Ticket('Diuna', 'H7', Money.of(p))));
        b.snacks.forEach((p) => cart.add(new step2Snack.Snack('Popcorn', Money.of(p))));
        return cart.summary();
      })
      .variant('step3', (b) => {
        const cart = new step3Cart.Cart();
        b.tickets.forEach((p) => cart.add(new step3Ticket.Ticket('Diuna', 'H7', Money.of(p))));
        b.snacks.forEach((p) => cart.add(new step3Snack.Snack('Popcorn', Money.of(p))));
        return cart.summary();
      })
      .expect('dwa bilety 2D i popcorn', { tickets: ['25.00', '25.00'], snacks: ['18.00'] },
        'Razem: 68.00, VAT: 7.07')
      .expect('bilet IMAX i napój', { tickets: ['40.00'], snacks: ['9.00'] },
        'Razem: 49.00, VAT: 4.64')
      .expect('sam bar', { tickets: [], snacks: ['9.00'] }, 'Razem: 9.00, VAT: 1.68')
      .expect('pusty koszyk', { tickets: [], snacks: [] }, 'Razem: 0.00, VAT: 0.00')
      .tests();
  });
});
