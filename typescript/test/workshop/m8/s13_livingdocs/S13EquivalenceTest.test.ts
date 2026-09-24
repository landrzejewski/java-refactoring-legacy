import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m8/s13_livingdocs/start/Routing.js';
import * as step1 from '../../../../src/workshop/m8/s13_livingdocs/step1/Routing.js';
import * as step2 from '../../../../src/workshop/m8/s13_livingdocs/step2/Routing.js';
import { Scene } from '../../support/scene.js';

function targetOf(routes: readonly { operation: string; target: string }[], operation: string): string {
  const route = routes.find((r) => r.operation === operation);
  if (route === undefined) {
    throw new Error('brak trasy ' + operation);
  }
  return route.target;
}

/** Test równoważności: praca nad dokumentacją nie zmienia routingu. */
describe('S13EquivalenceTest', () => {
  describe('routingStaysTheSame', () => {
    Scene.variants<string, string>()
      .variant('start', (op) => targetOf(start.Routing.routes(), op))
      .variant('step1', (op) => targetOf(step1.Routing.routes(), op))
      .variant('step2', (op) => targetOf(step2.Routing.routes(), op))
      .expect('rezerwacja', 'book', 'new')
      .expect('raport', 'report', 'new')
      .expect('anulowanie', 'cancel', 'legacy')
      .tests();
  });
});
