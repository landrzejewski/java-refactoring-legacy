import { describe, expect, it } from 'vitest';

import { BoundaryRule } from '../../../../src/workshop/m3/s13_boundarycheck/BoundaryRule.js';
import { CohesionProbe, CohesionResult } from '../../../../src/workshop/m3/s13_boundarycheck/CohesionProbe.js';
import { workshopDir } from '../../support/paths.js';

/**
 * Automatyczna ochrona granicy i diagnostyka spójności na plikach źródłowych sceny.
 * Start jest edytowany na żywo, więc dla niego stosujemy "zamrożone naruszenia"
 * (jak baseline znanych naruszeń w dependency-cruiser): znane naruszenia są tolerowane
 * i można je spłacać, każde NOWE naruszenie zapala czerwone światło. Kroki 1 i 2 sprawdzamy dokładnie.
 */
describe('S13ArchitectureTest', () => {
  const scene = (...parts: string[]): string => workshopDir('m3', 's13_boundarycheck', ...parts);
  const DOMAIN_IS_PURE = new BoundaryRule('/sql/', '/adapter/');
  /** Zamrożone zależności (bez nazwy pliku - przeniesienie kodu nie tworzy "nowego" naruszenia). */
  const FROZEN_START_IMPORTS = ['../../sql/Timestamp.js', '../adapter/ScreeningRow.js'];

  it('startDomainHasNoViolationsBeyondTheFrozenOnes', () => {
    const violations = DOMAIN_IS_PURE.violations(scene('start', 'domain'));
    console.log(`s13 start - naruszenia granicy: [${violations.join(', ')}]`);
    const imports = violations.map((v) => v.slice(v.indexOf(': ') + 2));
    expect(imports.every((i) => FROZEN_START_IMPORTS.includes(i)), `nowe naruszenie granicy: ${violations.join(', ')}`)
      .toBe(true);
  });

  it('step1StillViolatesTheBoundaryButOnlyInTheMapper', () => {
    expect(DOMAIN_IS_PURE.violations(scene('step1', 'domain'))).toEqual([
      'ScreeningRowMapper.ts: ../../sql/Timestamp.js',
      'ScreeningRowMapper.ts: ../adapter/ScreeningRow.js',
    ]);
  });

  it('step2DomainIsFreeOfTechnology', () => {
    expect(DOMAIN_IS_PURE.violations(scene('step2', 'domain'))).toEqual([]);
  });

  it('startServiceCohesionDoesNotGetWorse', () => {
    const result = new CohesionProbe().analyze(scene('start', 'domain', 'ScreeningService.ts'));
    console.log(`s13 start - LCOM4 ScreeningService: ${result.toString()}`);
    expect(result.lcom4 <= 2, `spojnosc sie pogorszyla: ${result.toString()}`).toBe(true);
  });

  it('step1ClassesAreCohesive', () => {
    const probe = new CohesionProbe();
    expect(probe.analyze(scene('step1', 'domain', 'ScreeningService.ts')))
      .toEqual(new CohesionResult(1, ['isMorning, price']));
    expect(probe.analyze(scene('step1', 'domain', 'ScreeningRowMapper.ts')))
      .toEqual(new CohesionResult(1, ['fromRow, toRow']));
  });
});
