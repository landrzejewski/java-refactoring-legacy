import { describe } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import { ScreeningRow } from '../../../../src/workshop/m6/s02_polymorphism/ScreeningRow.js';
import * as start from '../../../../src/workshop/m6/s02_polymorphism/start/Screening.js';
import * as step1 from '../../../../src/workshop/m6/s02_polymorphism/step1/Screening.js';
import * as step2 from '../../../../src/workshop/m6/s02_polymorphism/step2/Screening.js';
import * as step3 from '../../../../src/workshop/m6/s02_polymorphism/step3/Screening.js';
import type { Money } from '../../../../src/workshop/shared/Money.js';
import { Scene } from '../../support/scene.js';

interface Described {
  label(): string;
  durationMinutes(): number;
  price(): Money;
}

function describeScreening(s: Described): string {
  return `${s.label()}|${s.durationMinutes()}|${s.price().toString()}`;
}

function run(description: () => string): string {
  try {
    return description();
  } catch (error) {
    if (error instanceof IllegalArgumentError) {
      return `ERROR: ${error.message}`;
    }
    throw error;
  }
}

/** Jeden test kontraktowy dla wszystkich rodzajów seansu i wszystkich kroków. */
describe('S02EquivalenceTest', () => {
  describe('everyStepDescribesScreeningsTheSame', () => {
    Scene.variants<ScreeningRow, string>()
      .variant('start', (row) => run(() => describeScreening(start.Screening.fromRow(row))))
      .variant('step1', (row) => run(() => describeScreening(step1.Screening.fromRow(row))))
      .variant('step2', (row) => run(() => describeScreening(step2.Screening.fromRow(row))))
      .variant('step3', (row) => run(() => describeScreening(step3.Screening.fromRow(row))))
      .expect('zwykły seans', new ScreeningRow('REGULAR', 'Amator', 120), 'Amator|140|25.00')
      .expect('premiera', new ScreeningRow('PREMIERE', 'Diuna', 166), 'Premiera: Diuna|196|35.00')
      .expect('maraton 3 filmy', new ScreeningRow('MARATHON', 'Wladca Pierscieni', 3),
        'Maraton: Wladca Pierscieni (3 filmy)|390|60.00')
      .expect('maraton 1 film - bez przerw', new ScreeningRow('MARATHON', 'Diuna', 1),
        'Maraton: Diuna (1 filmy)|120|20.00')
      .expect('nieznany rodzaj', new ScreeningRow('DRIVE_IN', 'Amator', 120),
        'ERROR: unknown screening kind: DRIVE_IN')
      .tests();
  });
});
