import { describe, expect, it } from 'vitest';

import { Screening as Step1Screening } from '../../../../src/workshop/m5/s05_extractsubclass/step1/Screening.js';
import * as step4 from '../../../../src/workshop/m5/s05_extractsubclass/step4/Screening.js';
import { declaresMember, sourceOf, superclassOf } from '../reflection.js';

/** Extract Subclass celowo zmienia klasę runtime - to widzą constructor, instanceof, equals, JSON i switch. */
describe('S05SolutionTest', () => {
  it('beforeExtractionFactoriesReturnOneClass', () => {
    const premiere = Step1Screening.premiere('Amator', '2D', 'Anna Nowak');
    const regular = Step1Screening.regular('Amator', '2D');
    expect(premiere.constructor).toBe(regular.constructor);
  });

  it('solutionPicksRuntimeClassInFactory', () => {
    expect(step4.Screening.premiere('Amator', '2D', 'Anna Nowak').constructor).toBe(step4.PremiereScreening);
    expect(step4.Screening.regular('Amator', '2D').constructor).toBe(step4.Screening);
  });

  it('solutionKeepsPremiereStateOnlyInSubclass', () => {
    const source = sourceOf('s05_extractsubclass', 'step4', 'Screening.ts');
    expect(declaresMember(source, 'Screening', '#guest')).toBe(false);
    expect(declaresMember(source, 'Screening', '#premiere')).toBe(false);
    // Odpowiednik getPermittedSubclasses(): TS nie ma sealed, hierarchia jest zamknięta w module
    // Screening.ts - jedyną podklasą, którą ten moduł eksportuje, jest PremiereScreening.
    const subclasses = Object.values(step4).filter((type) => superclassOf(type) === step4.Screening);
    expect(subclasses).toEqual([step4.PremiereScreening]);
  });
});
