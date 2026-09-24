import { describe, expect, it } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import * as startPriceList from '../../../../src/workshop/m6/s17_singleton/start/PriceList.js';
import * as startDesk from '../../../../src/workshop/m6/s17_singleton/start/TicketDesk.js';
import * as step1 from '../../../../src/workshop/m6/s17_singleton/step1/PriceList.js';
import { TicketDesk } from '../../../../src/workshop/m6/s17_singleton/step3/TicketDesk.js';

/** Pomiar przed decyzją o cyklu życia i testowalność po wstrzyknięciu. */
describe('S17SolutionTest', () => {
  /** Pomiar w start (licznik instancji); po "warsztat.sh jump" licznika nie ma i test jest pomijany. */
  it('startCreatesAPriceListForEveryQuote', (context) => {
    // Odpowiednik refleksji z Javy: po "jump" start/PriceList może nie mieć metody created().
    const created: unknown = Reflect.get(startPriceList.PriceList, 'created');
    if (typeof created !== 'function') {
      context.skip('start nie ma już licznika instancji');
      return;
    }
    const count = (): number => created.call(startPriceList.PriceList) as number;
    const before = count();
    const desk = new startDesk.TicketDesk();
    desk.quote('2D', false);
    desk.quote('3D', false);
    desk.quote('IMAX', true);
    expect(count() - before).toBe(3);
  });

  it('singletonReturnsTheSameInstance', () => {
    expect(step1.PriceList.getInstance()).toBe(step1.PriceList.getInstance());
  });

  it('injectedTariffNeedsNoGlobalState', () => {
    const promo = new TicketDesk({ basePrice: () => Money.of('19.00') });
    expect(promo.quote('IMAX', true)).toEqual(Money.of('21.00'));
    expect(new TicketDesk().quote('IMAX', true), 'domyślny cennik bez zmian').toEqual(Money.of('42.00'));
  });
});
