import { existsSync } from 'node:fs';

import { describe, expect, it } from 'vitest';

import { ImaxHall as Step2ImaxHall } from '../../../../src/workshop/m5/s07_collapsehierarchy/step2/ImaxHall.js';
import { Hall } from '../../../../src/workshop/m5/s07_collapsehierarchy/step3/Hall.js';
import { workshopDir } from '../../support/paths.js';
import { declaredFields, sourceOf } from '../reflection.js';

/** Collapse Hierarchy: zbędny poziom znika, nazwa używana przez klientów (Hall) zostaje. */
describe('S07SolutionTest', () => {
  it('beforeCollapseSubclassHasNoOwnStateOrBehaviour', () => {
    // Pola: żadnej deklaracji pola w klasie; metody: na prototypie tylko constructor.
    expect(declaredFields(sourceOf('s07_collapsehierarchy', 'step2', 'ImaxHall.ts'), 'ImaxHall')).toHaveLength(0);
    expect(Object.getOwnPropertyNames(Step2ImaxHall.prototype)).toEqual(['constructor']);
  });

  it('solutionHasSingleFinalClass', () => {
    // Java sprawdza też Modifier.isFinal(Hall) - TS nie ma klas final.
    // Odpowiednik ClassNotFoundException: w kroku 3 nie ma modułu ImaxHall.
    expect(existsSync(workshopDir('m5', 's07_collapsehierarchy', 'step3', 'ImaxHall.ts'))).toBe(false);
    expect(Hall.imax('Sala IMAX', 14, 22).constructor).toBe(Hall);
  });
});
