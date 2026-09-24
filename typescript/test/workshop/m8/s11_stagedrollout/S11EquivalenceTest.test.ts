import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m8/s11_stagedrollout/start/CheckoutRouter.js';
import * as step1 from '../../../../src/workshop/m8/s11_stagedrollout/step1/CheckoutRouter.js';
import * as step2 from '../../../../src/workshop/m8/s11_stagedrollout/step2/CheckoutRouter.js';
import * as step3 from '../../../../src/workshop/m8/s11_stagedrollout/step3/CheckoutRouter.js';
import { Scene } from '../../support/scene.js';

/** Test równoważności: z dotychczasowymi ustawieniami każdy krok kieruje klientów tak samo. */
describe('S11EquivalenceTest', () => {
  describe('currentSettingsRouteTheSameCustomers', () => {
    Scene.variants<string, boolean>()
      .variant('start', (email) => new start.CheckoutRouter().useNewCheckout(email))
      .variant('step1', (email) => new step1.CheckoutRouter().useNewCheckout(email))
      .variant('step2', (email) => new step2.CheckoutRouter().useNewCheckout(email))
      .variant('step3', (email) => new step3.CheckoutRouter().useNewCheckout(email))
      .expect('tester Anna', 'anna@kino.pl', true)
      .expect('tester Jan', 'jan@kino.pl', true)
      .expect('klientka Ola', 'ola@kino.pl', false)
      .expect('klient Piotr', 'piotr@kino.pl', false)
      .tests();
  });
});
