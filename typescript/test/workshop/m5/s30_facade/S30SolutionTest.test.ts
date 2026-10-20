import { existsSync, readFileSync } from 'node:fs';

import { describe, expect, it } from 'vitest';

import { IllegalStateError } from '../../../../src/shared/errors.js';
import * as facade from '../../../../src/workshop/m5/s30_facade/step3/BookingFacade.js';
import { Cinema } from '../../../../src/workshop/m5/s30_facade/step3/Cinema.js';
import * as step2 from '../../../../src/workshop/m5/s30_facade/step2/Cinema.js';
import { TicketOrder } from '../../../../src/workshop/m5/s30_facade/TicketOrder.js';
import { workshopDir } from '../../support/paths.js';

const STEP3 = (file: string) => workshopDir('m5', 's30_facade', 'step3', file);
const TIMEOUT = new TicketOrder('S1', ['A1'], 'N', 'TIMEOUT', 'anna@example.com');

/** Krok 3: podsystem schowany, kanały znają tylko fasadę, niezmiennik trzyma się także przy awarii. */
describe('S30SolutionTest', () => {
  // TypeScript nie ma widoczności pakietowej: podsystemy to klasy prywatne modułu BookingFacade
  // (bez osobnych plików i bez eksportu), a konstruktor fasady jest prywatny.
  it('subsystemIsHiddenBehindTheFacade', () => {
    const source = readFileSync(STEP3('BookingFacade.ts'), 'utf8');
    for (const name of ['SeatRegistry', 'PriceList', 'PaymentClient', 'TicketMailer']) {
      expect(existsSync(STEP3(`${name}.ts`)), name).toBe(false);
      expect(source, name).toMatch(new RegExp(`^class ${name}\\b`, 'm'));
    }
    expect(Object.keys(facade)).toEqual(['BookingFacade']);
    expect(source, 'fasadę składa korzeń kompozycji').toMatch(/^ {2}private constructor\(/m);
  });

  // Odpowiednik refleksji z Javy (typy pól instancji): jedynym polem kanału jest fasada.
  it('salesChannelsKnowOnlyTheFacade', () => {
    const cinema = new Cinema();
    for (const [name, channel] of [['OnlineShop', cinema.onlineShop()], ['BoxOffice', cinema.boxOffice()]] as const) {
      const dependencies = Object.values(channel);
      expect(dependencies, name).toHaveLength(1);
      expect(dependencies[0], name).toBeInstanceOf(facade.BookingFacade);
    }
  });

  it('gatewayFailureNoLongerLeavesSeatsTaken', () => {
    const cinema = new Cinema();
    expect(() => cinema.onlineShop().buy(TIMEOUT)).toThrow(IllegalStateError);
    expect(cinema.state()).toBe('miejsca={S1=[]} platnosci=[] maile=[]');
  });

  it('beforeStep3AGatewayFailureLeakedSeats', () => {
    const cinema = new step2.Cinema();
    expect(() => cinema.onlineShop().buy(TIMEOUT)).toThrow(IllegalStateError);
    expect(cinema.state()).toBe('miejsca={S1=[A1]} platnosci=[] maile=[]');
  });
});
