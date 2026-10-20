import { describe, expect, it } from 'vitest';

import * as start from '../../../../src/workshop/m5/s30_facade/start/Cinema.js';
import * as step1 from '../../../../src/workshop/m5/s30_facade/step1/Cinema.js';
import * as step2 from '../../../../src/workshop/m5/s30_facade/step2/Cinema.js';
import * as step3 from '../../../../src/workshop/m5/s30_facade/step3/Cinema.js';
import { type AnyCinema, channels } from './S30Fixtures.js';

const DECLINED_AT_DESK = [
  'desk S1 A1 N 10000 jan@example.com', 'state', 'online S1 A1 N 4111 anna@example.com'];

const LEAK = [
  'ERROR payment declined',
  'miejsca={S1=[A1]} platnosci=[DECLINED 10000 25.00] maile=[]',
  'ERROR seat taken A1',
].join('\n');

const RELEASED = [
  'ERROR payment declined',
  'miejsca={S1=[]} platnosci=[DECLINED 10000 25.00] maile=[]',
  'OK 27.00',
].join('\n');

function play(cinema: AnyCinema): string {
  return DECLINED_AT_DESK.map(channels(cinema)).join('\n');
}

/**
 * Sens sceny: kopia orkiestracji w kasie zapomniała zwolnić miejsca po odmowie płatności.
 * Fasada w kroku 2 to naprawia - to świadoma zmiana zachowania, nie "czysta" refaktoryzacja.
 */
describe('S30BehaviourChangeTest', () => {
  /** Po "warsztat.sh jump" do kroku 2+ kasa nie ma już własnej orkiestracji i test jest pomijany. */
  it('startBoxOfficeLeaksSeatsAfterDeclinedPayment', (context) => {
    const cinema = new start.Cinema();
    // Odpowiednik refleksji z Javy (typy pól): czy któreś pole kasy jest rejestrem miejsc.
    const ownOrchestration = Object.values(cinema.boxOffice())
      .some((field: unknown) => field instanceof Object && field.constructor.name === 'SeatRegistry');
    if (!ownOrchestration) {
      context.skip('start kasy korzysta już z fasady');
      return;
    }
    expect(play(cinema)).toBe(LEAK);
  });

  it('step1StillLeaksBecauseOnlyTheShopUsesTheFacade', () => {
    expect(play(new step1.Cinema())).toBe(LEAK);
  });

  it('step2FacadeReleasesSeatsForBothChannels', () => {
    expect(play(new step2.Cinema())).toBe(RELEASED);
  });

  it('step3FacadeReleasesSeatsForBothChannels', () => {
    expect(play(new step3.Cinema())).toBe(RELEASED);
  });
});
