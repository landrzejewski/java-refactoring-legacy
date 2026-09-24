import { describe, expect, it } from 'vitest';

import { IllegalStateError } from '../../../../src/shared/errors.js';
import * as step1 from '../../../../src/workshop/m6/s06_builder/step1/ScheduleBuilder.js';
import { ScheduleBuilder } from '../../../../src/workshop/m6/s06_builder/step3/ScheduleBuilder.js';
import { LocalDate } from '../../../../src/workshop/shared/time.js';

const DAY = LocalDate.of(2026, 10, 3);

/** Niezmienniki buildera: jednorazowość, unikalne sale, niemutowalny wynik. */
describe('S06SolutionTest', () => {
  it('builderCannotBeReused', () => {
    const builder = ScheduleBuilder.day(DAY).hall('Sala 1', (hall) => hall.screening('Diuna', 18, 0));
    builder.build();
    expect(() => builder.build()).toThrow(IllegalStateError);
    expect(() => builder.hall('Sala 2', () => {})).toThrow(IllegalStateError);
  });

  it('hallNamesMustBeUnique', () => {
    const act = () => ScheduleBuilder.day(DAY)
      .hall('Sala 1', () => {})
      .hall('Sala 1', () => {});
    expect(act).toThrow(IllegalStateError);
    expect(act).toThrow('duplicate hall: Sala 1');
  });

  it('builtTreeIsImmutable', () => {
    const day = ScheduleBuilder.day(DAY).hall('Sala 1', (hall) => hall.screening('Diuna', 18, 0)).build();
    // zamrożona tablica: modyfikacja rzuca TypeError (w Javie: UnsupportedOperationException)
    expect(() => (day.halls as unknown[]).splice(0)).toThrow(TypeError);
    expect(() => (day.halls[0]!.screenings as unknown[]).splice(0)).toThrow(TypeError);
  });

  it('classicBuilderRejectsScreeningBeforeHall', () => {
    const builder = new step1.ScheduleBuilder(DAY);
    expect(() => builder.screening('Diuna', 18, 0)).toThrow(IllegalStateError);
  });
});
