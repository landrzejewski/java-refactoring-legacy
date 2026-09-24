import { describe } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import * as start from '../../../../src/workshop/m6/s03_typecode/start/ScreeningCsv.js';
import * as step1 from '../../../../src/workshop/m6/s03_typecode/step1/ScreeningCsv.js';
import * as step2 from '../../../../src/workshop/m6/s03_typecode/step2/ScreeningCsv.js';
import * as step3 from '../../../../src/workshop/m6/s03_typecode/step3/ScreeningCsv.js';
import { Scene } from '../../support/scene.js';

function safe(describeLine: (line: string) => string): (line: string) => string {
  return (line) => {
    try {
      return describeLine(line);
    } catch (error) {
      if (error instanceof IllegalArgumentError) {
        return `ERROR: ${error.message}`;
      }
      throw error;
    }
  };
}

/** Odczyt CSV, zachowanie formatu i zapis zwrotny tego samego kodu liczbowego. */
describe('S03EquivalenceTest', () => {
  describe('everyStepReadsAndWritesTheSameCsv', () => {
    const startCsv = new start.ScreeningCsv();
    const step1Csv = new step1.ScreeningCsv();
    const step2Csv = new step2.ScreeningCsv();
    const step3Csv = new step3.ScreeningCsv();
    Scene.variants<string, string>()
      .variant('start', safe((line) => startCsv.describe(line)))
      .variant('step1', safe((line) => step1Csv.describe(line)))
      .variant('step2', safe((line) => step2Csv.describe(line)))
      .variant('step3', safe((line) => step3Csv.describe(line)))
      .expect('2D', 'Amator;1', 'Amator|2D|25.00|okulary:nie|csv=Amator;1')
      .expect('3D', 'Kraina Lodu;2', 'Kraina Lodu|3D|32.00|okulary:tak|csv=Kraina Lodu;2')
      .expect('IMAX ze spacją', 'Diuna; 3', 'Diuna|IMAX|40.00|okulary:nie|csv=Diuna;3')
      .expect('nieznany kod', 'Diuna;7', 'ERROR: unknown format code: 7')
      .expect('kod 0', 'Diuna;0', 'ERROR: unknown format code: 0')
      .expect('kod nie jest liczbą', 'Diuna;x', 'ERROR: For input string: "x"')
      .tests();
  });
});
