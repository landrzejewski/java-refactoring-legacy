import { describe } from 'vitest';

import { LocalDateTime } from '../../../../src/workshop/shared/time.js';
import * as start from '../../../../src/workshop/m3/s13_boundarycheck/start/CinemaApp.js';
import * as step1 from '../../../../src/workshop/m3/s13_boundarycheck/step1/CinemaApp.js';
import * as step2 from '../../../../src/workshop/m3/s13_boundarycheck/step2/CinemaApp.js';
import { Scene } from '../../support/scene.js';

interface Case {
  readonly title: string;
  readonly start: LocalDateTime;
}

function of(app: (title: string, start: LocalDateTime) => string): (c: Case) => string {
  return (c) => app(c.title, c.start);
}

/** Naprawa granicy nie zmienia ceny ani mapowania na wiersz i z powrotem. */
describe('S13EquivalenceTest', () => {
  describe('everyStepPricesAndMapsTheSame', () => {
    Scene.variants<Case, string>()
      .variant('start', of(start.describe))
      .variant('step1', of(step1.describe))
      .variant('step2', of(step2.describe))
      .expect('seans wieczorny', { title: 'Amator', start: LocalDateTime.of(2026, 10, 2, 20, 0) },
        '25.00 | ScreeningRow[table=screenings, title=Amator, start=2026-10-02 20:00:00.0] | true')
      .expect('seans poranny', { title: 'Kraina Lodu', start: LocalDateTime.of(2026, 10, 3, 10, 30) },
        '20.00 | ScreeningRow[table=screenings, title=Kraina Lodu, start=2026-10-03 10:30:00.0] | true')
      .tests();
  });
});
