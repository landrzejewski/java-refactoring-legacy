import { existsSync, statSync } from 'node:fs';
import { pathToFileURL } from 'node:url';

import { GateInput, type TestSuite } from '../../../../src/workshop/m8/s10_qualitygate/GateInput.js';
import { testDir, workshopDir } from '../../support/paths.js';

// Próbki są wyłączone z typechecku projektu (tsconfig "exclude"), więc ładujemy je dynamicznie
// - odpowiednik Class.forName(testClass) w Javie.
async function input(sources: string, tests: string): Promise<GateInput> {
  const sourceDir = workshopDir('m8', 's10_qualitygate', 'sample', sources);
  const testFile = testDir('m8', 's10_qualitygate', 'sample', tests, 'PriceTableTest.ts');
  if (!existsSync(sourceDir) || !statSync(sourceDir).isDirectory() || !existsSync(testFile)) {
    throw new Error('brak ' + sourceDir + ' albo ' + testFile);
  }
  const module = await import(pathToFileURL(testFile).href) as { PriceTableTest: TestSuite };
  return new GateInput(sourceDir, testFile, 'PriceTable', module.PriceTableTest);
}

export const DIRTY = await input('dirty', 'dirty');
export const CLEAN = await input('clean', 'clean');
export const FAILING_TESTS = await input('clean', 'broken');
