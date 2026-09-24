import { describe } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import { Screening } from '../../../../src/workshop/m7/s05_breakmethod/Screening.js';
import * as start from '../../../../src/workshop/m7/s05_breakmethod/start/RepertoireBuilder.js';
import * as step1 from '../../../../src/workshop/m7/s05_breakmethod/step1/RepertoireBuilder.js';
import * as step2 from '../../../../src/workshop/m7/s05_breakmethod/step2/RepertoireBuilder.js';
import * as step3 from '../../../../src/workshop/m7/s05_breakmethod/step3/RepertoireBuilder.js';
import { LocalTime } from '../../../../src/workshop/shared/time.js';
import { Scene } from '../../support/scene.js';

/**
 * Test równoważności: ten sam tekst, ten sam wyjątek (typ i komunikat)
 * i nietknięta lista wejściowa w start i w każdym kroku.
 */
const DIUNA = new Screening('Diuna', 'IMAX', LocalTime.of(20, 0), 1, false);
const KRAINA = new Screening('Kraina Lodu', '3D', LocalTime.of(11, 0), 2, false);
const AMATOR = new Screening('Amator', '2D', LocalTime.of(18, 30), 3, false);
const AMATOR_LATE = new Screening('Amator', '2D', LocalTime.of(21, 0), 3, true);
const ALIEN = new Screening('Alien', '2D', LocalTime.of(20, 0), 4, false);

type Input = readonly (Screening | null)[];

/** Wektor zachowania: wynik albo wyjątek, plus to, czy lista klienta nie została zmieniona. */
function observe(build: (screenings: (Screening | null)[]) => string): (input: Input) => string {
  return (input) => {
    const clientList = [...input];
    const before = [...clientList];
    let result: string;
    try {
      result = build(clientList);
    } catch (error) {
      if (!(error instanceof IllegalArgumentError)) {
        throw error;
      }
      result = `IllegalArgumentError: ${error.message} `;
    }
    const unchanged = before.length === clientList.length && before.every((s, i) => s === clientList[i]);
    return `${result}| wejscie bez zmian: ${unchanged}`;
  };
}

describe('S05EquivalenceTest', () => {
  describe('everyStepBuildsTheSameRepertoire', () => {
    Scene.variants<Input, string>()
      .variant('start', observe((s) => new start.RepertoireBuilder().build(s)))
      .variant('step1', observe((s) => new step1.RepertoireBuilder().build(s)))
      .variant('step2', observe((s) => new step2.RepertoireBuilder().build(s)))
      .variant('step3', observe((s) => new step3.RepertoireBuilder().build(s)))
      .expect('sortowanie po godzinie, potem tytule; odwolany pominiety',
        [DIUNA, KRAINA, AMATOR_LATE, AMATOR, ALIEN],
        'REPERTUAR\n'
          + '11:00 Kraina Lodu (3D), sala 2\n'
          + '18:30 Amator (2D), sala 3\n'
          + '20:00 Alien (2D), sala 4\n'
          + '20:00 Diuna (IMAX), sala 1\n'
          + '| wejscie bez zmian: true')
      .expect('same odwolane',
        [AMATOR_LATE],
        'REPERTUAR\n'
          + 'brak seansow\n'
          + '| wejscie bez zmian: true')
      .expect('null w srodku listy',
        [DIUNA, null, KRAINA],
        'IllegalArgumentError: screening must not be null | wejscie bez zmian: true')
      .tests();
  });
});
