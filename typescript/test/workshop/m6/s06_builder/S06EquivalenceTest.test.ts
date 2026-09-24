import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m6/s06_builder/start/WeekendPlanner.js';
import * as step1 from '../../../../src/workshop/m6/s06_builder/step1/WeekendPlanner.js';
import * as step2 from '../../../../src/workshop/m6/s06_builder/step2/WeekendPlanner.js';
import * as step3 from '../../../../src/workshop/m6/s06_builder/step3/WeekendPlanner.js';
import { LocalDate } from '../../../../src/workshop/shared/time.js';
import { Scene } from '../../support/scene.js';

/** Repertuar dnia zbudowany przez każdy wariant renderuje się identycznie. */
describe('S06EquivalenceTest', () => {
  describe('everyStepBuildsTheSameSchedule', () => {
    Scene.variants<LocalDate, string>()
      .variant('start', (d) => new start.WeekendPlanner().plan(d).render())
      .variant('step1', (d) => new step1.WeekendPlanner().plan(d).render())
      .variant('step2', (d) => new step2.WeekendPlanner().plan(d).render())
      .variant('step3', (d) => new step3.WeekendPlanner().plan(d).render())
      .expect('sobota', LocalDate.of(2026, 10, 3), `2026-10-03 SATURDAY
Sala 1
  18:00 Diuna
  21:00 Diuna
Sala 2
  10:00 Kraina Lodu
  17:30 Amator
Sala 3 VIP
  20:00 Amator
Seansow: 5
`)
      .expect('piątek - pusta sala VIP', LocalDate.of(2026, 10, 2), `2026-10-02 FRIDAY
Sala 1
  18:00 Diuna
  21:00 Diuna
Sala 2
  17:30 Amator
Sala 3 VIP
  (brak seansow)
Seansow: 3
`)
      .tests();
  });
});
