import { describe, expect, it } from 'vitest';

import { BookingRequest } from '../../../../src/workshop/m8/s04_shadowlimits/BookingRequest.js';
import { Infrastructure } from '../../../../src/workshop/m8/s04_shadowlimits/Infrastructure.js';
import * as start from '../../../../src/workshop/m8/s04_shadowlimits/start/ShadowBooking.js';
import * as step1 from '../../../../src/workshop/m8/s04_shadowlimits/step1/ShadowBooking.js';
import * as step2 from '../../../../src/workshop/m8/s04_shadowlimits/step2/ShadowBooking.js';
import { NewBookingFlow } from '../../../../src/workshop/m8/s04_shadowlimits/step3/NewBookingFlow.js';
import { RealEffects } from '../../../../src/workshop/m8/s04_shadowlimits/step3/RealEffects.js';
import * as step3 from '../../../../src/workshop/m8/s04_shadowlimits/step3/ShadowBooking.js';

const ANNA = new BookingRequest('anna@kino.pl', '4111-1111', 'Amator', 2);
const LEGACY_EFFECTS = [
  'CHARGE 4111-1111: 54.00',
  'SAVE Amator;anna@kino.pl;2;54.00',
  'MAIL anna@kino.pl: Bilety Amator x2, zaplacono 54.00'];
const MAIL_DIVERGENCE = 'efekty legacy: MAIL anna@kino.pl: Bilety Amator x2, zaplacono 54.00'
  + ' | kandydat: MAIL anna@kino.pl: Bilety Amator x2, zaplacono 50.00';

/** Efekty uboczne w trybie shadow: od podwójnych maili i obciążeń do porównania zamiarów. */
describe('S04SolutionTest', () => {
  it('startShadowChargesTwiceAndSendsTwoMailsWithoutNoticingAnything', () => {
    const infra = new Infrastructure();
    const shadow = new start.ShadowBooking(infra);
    shadow.book(ANNA);
    expect(infra.count('MAIL'), 'klient dostał dwa maile').toBe(2);
    expect(infra.count('CHARGE'), 'karta obciążona dwa razy').toBe(2);
    expect(infra.count('SAVE'), 'dwa wiersze w bazie').toBe(2);
    expect(shadow.divergences(), 'wynik ten sam, więc cień niczego nie widzi').toEqual([]);
  });

  it('step1PreparatoryRefactoringKeepsTheBugOnPurpose', () => {
    const infra = new Infrastructure();
    new step1.ShadowBooking(infra).book(ANNA);
    expect(infra.count('MAIL')).toBe(2);
    expect(infra.count('CHARGE')).toBe(2);
  });

  it('step2RecordsCandidateEffectsInsteadOfExecutingThem', () => {
    const infra = new Infrastructure();
    const shadow = new step2.ShadowBooking(infra);
    shadow.book(ANNA);
    expect(infra.log(), 'tylko efekty legacy').toEqual(LEGACY_EFFECTS);
    expect(shadow.divergences(), 'porównanie efektów znalazło błąd w mailu').toEqual([MAIL_DIVERGENCE]);
  });

  it('step3ComparesAPurePlanAndNeverTouchesInfrastructure', () => {
    const infra = new Infrastructure();
    const shadow = new step3.ShadowBooking(infra);
    shadow.book(ANNA);
    expect(infra.log()).toEqual(LEGACY_EFFECTS);
    expect(shadow.divergences()).toEqual([MAIL_DIVERGENCE]);
  });

  it('step3NewFlowStillExecutesItsPlanWhenItIsAuthoritative', () => {
    const infra = new Infrastructure();
    const flow = new NewBookingFlow(new RealEffects(infra));
    expect(flow.book(ANNA)).toBe('OK 54.00');
    expect(infra.log()).toHaveLength(3);
  });
});
