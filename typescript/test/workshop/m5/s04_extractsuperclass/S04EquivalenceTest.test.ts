import { describe } from 'vitest';

import { LocalDateTime } from '../../../../src/workshop/shared/time.js';
import * as startEvent from '../../../../src/workshop/m5/s04_extractsuperclass/start/PrivateEvent.js';
import * as startPlanner from '../../../../src/workshop/m5/s04_extractsuperclass/start/HallPlanner.js';
import * as startScreening from '../../../../src/workshop/m5/s04_extractsuperclass/start/Screening.js';
import * as step1Event from '../../../../src/workshop/m5/s04_extractsuperclass/step1/PrivateEvent.js';
import * as step1Planner from '../../../../src/workshop/m5/s04_extractsuperclass/step1/HallPlanner.js';
import * as step1Screening from '../../../../src/workshop/m5/s04_extractsuperclass/step1/Screening.js';
import * as step2Event from '../../../../src/workshop/m5/s04_extractsuperclass/step2/PrivateEvent.js';
import * as step2Planner from '../../../../src/workshop/m5/s04_extractsuperclass/step2/HallPlanner.js';
import * as step2Screening from '../../../../src/workshop/m5/s04_extractsuperclass/step2/Screening.js';
import * as step3Event from '../../../../src/workshop/m5/s04_extractsuperclass/step3/PrivateEvent.js';
import * as step3Planner from '../../../../src/workshop/m5/s04_extractsuperclass/step3/HallPlanner.js';
import * as step3Screening from '../../../../src/workshop/m5/s04_extractsuperclass/step3/Screening.js';
import { Scene } from '../../support/scene.js';

/** Rezerwacja w teście: seans (minutes > 0) albo standardowy wynajem (minutes == 0). */
class Booking {
  constructor(readonly name: string, readonly hall: string, readonly start: string, readonly minutes: number) {}

  rental(): boolean {
    return this.minutes === 0;
  }

  at(): LocalDateTime {
    return LocalDateTime.parse(this.start);
  }
}

/** Wspólny kształt każdego wariantu sceny: konstruktor seansu, fabryka wynajmu i planista. */
interface Variant<S, E> {
  screening: new (title: string, hall: string, start: LocalDateTime, minutes: number) => S;
  rental: (client: string, hall: string, start: LocalDateTime) => E;
  planner: { conflicts(screenings: readonly S[], events: readonly E[]): string[] };
}

function play<S, E>(bookings: readonly Booking[], variant: Variant<S, E>): string {
  const screenings: S[] = [];
  const events: E[] = [];
  for (const b of bookings) {
    if (b.rental()) {
      events.push(variant.rental(b.name, b.hall, b.at()));
    } else {
      screenings.push(new variant.screening(b.name, b.hall, b.at(), b.minutes));
    }
  }
  return variant.planner.conflicts(screenings, events).join('; ');
}

/** Test równoważności: lista konfliktów w salach jest identyczna w start i każdym kroku. */
describe('S04EquivalenceTest', () => {
  describe('everyStepFindsTheSameConflicts', () => {
    Scene.variants<readonly Booking[], string>()
      .variant('start', (b) => play(b, {
        screening: startScreening.Screening, rental: startEvent.PrivateEvent.rental, planner: new startPlanner.HallPlanner(),
      }))
      .variant('step1', (b) => play(b, {
        screening: step1Screening.Screening, rental: step1Event.PrivateEvent.rental, planner: new step1Planner.HallPlanner(),
      }))
      .variant('step2', (b) => play(b, {
        screening: step2Screening.Screening, rental: step2Event.PrivateEvent.rental, planner: new step2Planner.HallPlanner(),
      }))
      .variant('step3', (b) => play(b, {
        screening: step3Screening.Screening, rental: step3Event.PrivateEvent.rental, planner: new step3Planner.HallPlanner(),
      }))
      .expect('seans nachodzi na wynajem', [
        new Booking('Diuna', 'Sala 1', '2026-10-02T18:00', 166),
        new Booking('Firma X', 'Sala 1', '2026-10-02T20:00', 0)],
      'Diuna x Wynajem: Firma X')
      .expect('różne sale - brak konfliktu', [
        new Booking('Diuna', 'Sala 1', '2026-10-02T18:00', 166),
        new Booking('Firma X', 'Sala 2', '2026-10-02T18:00', 0)],
      '')
      .expect('dwa seanse i dwa wynajmy', [
        new Booking('Kraina Lodu', 'Sala 2', '2026-10-02T18:30', 102),
        new Booking('Amator', 'Sala 2', '2026-10-02T17:00', 100),
        new Booking('Firma Y', 'Sala 3', '2026-10-02T11:00', 0),
        new Booking('Firma X', 'Sala 3', '2026-10-02T10:00', 0)],
      'Kraina Lodu x Amator; Wynajem: Firma Y x Wynajem: Firma X')
      .expect('koniec o 20:00 i start o 20:00 - styk to nie konflikt', [
        new Booking('Amator', 'Sala 1', '2026-10-02T18:00', 120),
        new Booking('Firma X', 'Sala 1', '2026-10-02T20:00', 0)],
      '')
      .tests();
  });
});
