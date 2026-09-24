import { describe } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import { StandardTicket } from '../../../../src/workshop/m5/s12_bridgemethods/StandardTicket.js';
import { StudentTicket } from '../../../../src/workshop/m5/s12_bridgemethods/StudentTicket.js';
import type { Ticket } from '../../../../src/workshop/m5/s12_bridgemethods/Ticket.js';
import * as start from '../../../../src/workshop/m5/s12_bridgemethods/start/RuleRegistry.js';
import * as step1 from '../../../../src/workshop/m5/s12_bridgemethods/step1/RuleRegistry.js';
import * as step2 from '../../../../src/workshop/m5/s12_bridgemethods/step2/RuleRegistry.js';
import { Scene } from '../../support/scene.js';

const show = (types: readonly string[]) => `[${types.join(', ')}]`;

/** Test równoważności: cena i lista obsługiwanych typów identyczne w start i każdym kroku. */
describe('S12EquivalenceTest', () => {
  describe('everyStepPricesAndReportsTheSameWay', () => {
    Scene.variants<Ticket, string>()
      .variant('start', (t) => {
        const registry = start.RuleRegistry.standard();
        return registry.price(t) + ' ' + show(registry.supportedTypes());
      })
      .variant('step1', (t) => {
        const registry = step1.RuleRegistry.standard();
        return registry.price(t) + ' ' + show(registry.supportedTypes());
      })
      .variant('step2', (t) => {
        const registry = step2.RuleRegistry.standard();
        return registry.price(t) + ' ' + show(registry.supportedTypes());
      })
      .expect('normalny IMAX', new StandardTicket(Money.of('40.00')),
        '40.00 [StandardTicket, StudentTicket]')
      .expect('studencki 2D', new StudentTicket(Money.of('25.00'), 'S-123'),
        '18.75 [StandardTicket, StudentTicket]')
      .tests();
  });
});
