import { describe, expect, it } from 'vitest';

import { assertNever } from '../../../../src/shared/assertNever.js';
import { ScreeningRow } from '../../../../src/workshop/m6/s02_polymorphism/ScreeningRow.js';
import { MarathonScreening } from '../../../../src/workshop/m6/s02_polymorphism/step3/MarathonScreening.js';
import { PremiereScreening } from '../../../../src/workshop/m6/s02_polymorphism/step3/PremiereScreening.js';
import { RegularScreening } from '../../../../src/workshop/m6/s02_polymorphism/step3/RegularScreening.js';
import { Screening } from '../../../../src/workshop/m6/s02_polymorphism/step3/Screening.js';

function badge(screening: Screening): string {
  switch (screening.kind) {
    case 'REGULAR': return 'zwykly';
    case 'PREMIERE': return 'premiera';
    case 'MARATHON': return 'maraton';
    default: return assertNever(screening);
  }
}

/** Po refaktoryzacji każdy rodzaj ma własne dane, a switch klienta jest sprawdzany przez kompilator. */
describe('S02SolutionTest', () => {
  it('mappingCreatesTheRightSubtypeWithNamedData', () => {
    const marathon: Screening = Screening.fromRow(new ScreeningRow('MARATHON', 'Diuna', 2));
    expect(marathon).toStrictEqual(new MarathonScreening('Diuna', 2));
  });

  it('clientSwitchIsExhaustiveWithoutDefault', () => {
    // dodanie czwartego rodzaju do unii Screening zepsuje kompilację funkcji badge (assertNever) - to zaleta i koszt
    expect(badge(new RegularScreening('Amator', 120))).toBe('zwykly');
    expect(badge(new PremiereScreening('Diuna', 166))).toBe('premiera');
    expect(badge(new MarathonScreening('Diuna', 2))).toBe('maraton');
  });
});
