import { describe, expect, it } from 'vitest';

import { TicketOrder } from '../../../../src/workshop/m6/s07_decorator/TicketOrder.js';
import { Insurance } from '../../../../src/workshop/m6/s07_decorator/step3/Insurance.js';
import type { PricedTicket } from '../../../../src/workshop/m6/s07_decorator/step3/PricedTicket.js';
import { Ticket } from '../../../../src/workshop/m6/s07_decorator/step3/Ticket.js';
import { TicketAssembler } from '../../../../src/workshop/m6/s07_decorator/step3/TicketAssembler.js';
import { VipSeat } from '../../../../src/workshop/m6/s07_decorator/step3/VipSeat.js';
import { Money } from '../../../../src/workshop/shared/Money.js';

/**
 * Granice przezroczystości dekoratora: instanceof, równość, kolejność.
 * Równość rekordów z Javy (equals) -> równość strukturalna z typem (toStrictEqual).
 */
describe('S07SolutionTest', () => {
  const core = new Ticket('Diuna', 'IMAX', Money.of('40.00'));

  it('instanceofSeesOnlyTheOutermostDecorator', () => {
    const ticket: PricedTicket = new TicketAssembler().assemble(
      new TicketOrder('Diuna', 'IMAX', Money.of('40.00'), true, false, true));
    expect(ticket instanceof Insurance).toBe(true);
    // VIP jest ukryty wewnątrz - pytanie 'czy VIP?' wymaga innego API
    expect(ticket instanceof VipSeat).toBe(false);
  });

  it('decoratedTicketIsNotEqualToItsCoreDespiteSameTitle', () => {
    expect(new VipSeat(core)).not.toStrictEqual(core);
  });

  it('orderOfDecoratorsChangesDescriptionButNotPrice', () => {
    const a: PricedTicket = new Insurance(new VipSeat(core));
    const b: PricedTicket = new VipSeat(new Insurance(core));
    expect(a.price().equals(b.price())).toBe(true);
    expect(a.description()).not.toBe(b.description());
    expect(a).not.toStrictEqual(b);
  });
});
