import { mkdtempSync, rmSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';

import { afterEach, beforeEach, describe, expect, it } from 'vitest';

import { BoundaryRule } from '../../../../src/workshop/m3/s13_boundarycheck/BoundaryRule.js';
import { CohesionProbe, CohesionResult } from '../../../../src/workshop/m3/s13_boundarycheck/CohesionProbe.js';

/** Testy samych narzędzi na stałych próbkach kodu (niezależnie od edycji start na żywo). */
describe('S13ToolsTest', () => {
  let dir = '';

  beforeEach(() => {
    dir = mkdtempSync(join(tmpdir(), 's13-'));
  });

  afterEach(() => {
    rmSync(dir, { recursive: true, force: true });
  });

  it('probeFindsTwoConceptsInOneClass', () => {
    const file = join(dir, 'Mixed.ts');
    writeFileSync(file, `export class Mixed {
  private readonly table: string;

  constructor(private readonly price: number, table: string) {
    this.table = table;
  }

  total(seats: number): number {
    return this.price * seats + this.fee();
  }

  private fee(): number {
    return 2;
  }

  row(title: string): string {
    return \`\${this.table};\${title}\`;
  }
}
`);
    expect(new CohesionProbe().analyze(file)).toEqual(new CohesionResult(2, ['fee, total', 'row']));
  });

  it('boundaryRuleReportsForbiddenImportsIncludingTypeOnly', () => {
    writeFileSync(join(dir, 'Policy.ts'), `import type { Db } from '../adapter/Db.js';
import { Connection } from '../sql/Connection.js';
import { List } from './List.js';
`);
    expect(new BoundaryRule('/sql/', '/adapter/').violations(dir))
      .toEqual(['Policy.ts: ../adapter/Db.js', 'Policy.ts: ../sql/Connection.js']);
  });
});
