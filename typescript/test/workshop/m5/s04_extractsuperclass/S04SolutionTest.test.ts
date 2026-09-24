import { describe, expect, it } from 'vitest';

import { LocalDateTime } from '../../../../src/workshop/shared/time.js';
import { HallBooking as Step1HallBooking } from '../../../../src/workshop/m5/s04_extractsuperclass/step1/HallBooking.js';
import { PrivateEvent as Step1PrivateEvent } from '../../../../src/workshop/m5/s04_extractsuperclass/step1/PrivateEvent.js';
import { Screening as Step1Screening } from '../../../../src/workshop/m5/s04_extractsuperclass/step1/Screening.js';
import { HallBooking } from '../../../../src/workshop/m5/s04_extractsuperclass/step3/HallBooking.js';
import { PrivateEvent } from '../../../../src/workshop/m5/s04_extractsuperclass/step3/PrivateEvent.js';
import { Screening } from '../../../../src/workshop/m5/s04_extractsuperclass/step3/Screening.js';
import { classModifiersOf, declaringClassOf, sourceOf, superclassOf } from '../reflection.js';

const EVENING = LocalDateTime.parse('2026-10-02T20:00');

/** Extract Superclass zmienia model typów: nadklasę, typ deklarujący akcesorów, wspólny kontrakt. */
describe('S04SolutionTest', () => {
  it('classesJoinTheSuperclassOneAtATime', () => {
    expect(superclassOf(Step1Screening)).toBe(Step1HallBooking);
    expect(superclassOf(Step1PrivateEvent)).toBeUndefined();
  });

  it('solutionSharesAbstractBookingWithDomainName', () => {
    expect(superclassOf(Screening)).toBe(HallBooking);
    expect(superclassOf(PrivateEvent)).toBe(HallBooking);
    expect(classModifiersOf(sourceOf('s04_extractsuperclass', 'step3', 'HallBooking.ts'), 'HallBooking')).toContain('abstract');
    expect(declaringClassOf(PrivateEvent, 'hall')).toBe(HallBooking);
  });

  it('overlapsIsSymmetricAcrossBookingTypes', () => {
    const screening: HallBooking = new Screening('Diuna', 'Sala 1', EVENING.minusHours(2), 166);
    const rental: HallBooking = PrivateEvent.rental('Firma X', 'Sala 1', EVENING);
    expect(screening.overlaps(rental)).toBe(true);
    expect(rental.overlaps(screening)).toBe(true);
    expect(rental.overlaps(PrivateEvent.rental('Firma Y', 'Sala 1', EVENING.plusHours(2)))).toBe(false);
  });
});
