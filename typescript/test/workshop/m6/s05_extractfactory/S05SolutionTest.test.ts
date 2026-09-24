import { describe, expect, it } from 'vitest';

import { ReservationFactory } from '../../../../src/workshop/m6/s05_extractfactory/step3/ReservationFactory.js';
import { ReservationService } from '../../../../src/workshop/m6/s05_extractfactory/step3/ReservationService.js';
import { fixedClock, LocalDateTime } from '../../../../src/workshop/shared/time.js';

/** Fabryka jest testowalna sama, a serwis dostaje ją jako zwykłą zależność. */
describe('S05SolutionTest', () => {
  const morning = fixedClock(LocalDateTime.parse('2026-10-03T09:50:00'));

  it('factoryAloneKnowsFeesAndExpiry', () => {
    const reservation = new ReservationFactory(morning).create('ONLINE', 'anna@kino.pl', ['A1', 'A2']);
    expect(reservation.id).toBe('R1');
    expect(reservation.fee.toString()).toBe('4.00');
    expect(reservation.expiresAt).toEqual(LocalDateTime.of(2026, 10, 3, 10, 5));
  });

  it('twoServicesSharingOneFactoryShareNumbering', () => {
    const factory = new ReservationFactory(morning);
    new ReservationService(factory).reserve('BOX_OFFICE', 'jan@kino.pl', ['A1']);
    const second = new ReservationService(factory).reserve('BOX_OFFICE', 'jan@kino.pl', ['A1']);
    expect(second.id).toBe('R2');
  });
});
