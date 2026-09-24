import { existsSync, statSync } from 'node:fs';

import { describe, expect, it } from 'vitest';

import { CompilerGate } from '../../../../src/workshop/m8/s08_compilergate/CompilerGate.js';
import { workshopDir } from '../../support/paths.js';

function variant(name: string): string {
  const dir = workshopDir('m8', 's08_compilergate', name);
  expect(existsSync(dir) && statSync(dir).isDirectory(), 'brak katalogu ' + dir).toBe(true);
  return dir;
}

/** Bramka kompilatora na kodzie sceny: każdy krok usuwa jedną klasę ostrzeżeń. */
describe('S08SolutionTest', () => {
  it('startFailsTheGateWithFourKindsOfWarnings', () => {
    const result = CompilerGate.check(variant('start'));
    expect(result.passed).toBe(false);
    expect(result.categories()).toEqual(['deprecation', 'fallthrough', 'rawtypes', 'unchecked']);
  });

  it('step1RemovesRawTypesAndUncheckedOperations', () => {
    const result = CompilerGate.check(variant('step1'));
    expect(result.passed).toBe(false);
    expect(result.categories()).toEqual(['deprecation', 'fallthrough']);
  });

  it('step2StopsUsingDeprecatedApi', () => {
    const result = CompilerGate.check(variant('step2'));
    expect(result.passed).toBe(false);
    expect(result.categories()).toEqual(['fallthrough']);
  });

  it('step3PassesTheGate', () => {
    const result = CompilerGate.check(variant('step3'));
    expect(result.warnings).toEqual([]);
    expect(result.passed).toBe(true);
  });

  it('warningsPointToFileAndLine', () => {
    const warnings = CompilerGate.check(variant('step2')).warnings.map((warning) => warning.toString());
    expect(warnings).toEqual(['[fallthrough] OccupancyReport.ts:13']);
  });
}, 60_000);
