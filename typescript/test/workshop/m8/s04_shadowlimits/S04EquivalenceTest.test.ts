import { describe } from 'vitest';

import { BookingRequest } from '../../../../src/workshop/m8/s04_shadowlimits/BookingRequest.js';
import { Infrastructure } from '../../../../src/workshop/m8/s04_shadowlimits/Infrastructure.js';
import * as start from '../../../../src/workshop/m8/s04_shadowlimits/start/ShadowBooking.js';
import * as step1 from '../../../../src/workshop/m8/s04_shadowlimits/step1/ShadowBooking.js';
import * as step2 from '../../../../src/workshop/m8/s04_shadowlimits/step2/ShadowBooking.js';
import * as step3 from '../../../../src/workshop/m8/s04_shadowlimits/step3/ShadowBooking.js';
import { Scene } from '../../support/scene.js';

/** Test równoważności: odpowiedź dla klienta (wynik legacy) jest taka sama w każdym kroku. */
describe('S04EquivalenceTest', () => {
  describe('customerGetsTheSameAnswer', () => {
    Scene.variants<BookingRequest, string>()
      .variant('start', (r) => new start.ShadowBooking(new Infrastructure()).book(r))
      .variant('step1', (r) => new step1.ShadowBooking(new Infrastructure()).book(r))
      .variant('step2', (r) => new step2.ShadowBooking(new Infrastructure()).book(r))
      .variant('step3', (r) => new step3.ShadowBooking(new Infrastructure()).book(r))
      .expect('2 bilety online', new BookingRequest('anna@kino.pl', '4111-1111', 'Amator', 2), 'OK 54.00')
      .expect('1 bilet online', new BookingRequest('jan@kino.pl', '5500-2222', 'Diuna', 1), 'OK 27.00')
      .tests();
  });
});
