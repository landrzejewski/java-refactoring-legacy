import { readdirSync, readFileSync } from 'node:fs';
import path from 'node:path';

import type { GateInput } from '../GateInput.js';

/**
 * Krok 1: pierwszy wykonywalny punkt listy - skan źródeł domeny: znaczniki TODO/FIXME
 * i wydruki console. Tanie, deterministyczne, z plikiem i linią.
 */
export class QualityGate {
  /** Lista wyników bramki; pusta lista = bramka przepuszcza zmianę. */
  evaluate(input: GateInput): string[] {
    const findings: string[] = [];
    QualityGate.scanSources(input, findings);
    return findings;
  }

  passes(input: GateInput): boolean {
    return this.evaluate(input).length === 0;
  }

  /** TODO/FIXME i wydruki na konsolę w źródłach domeny. */
  private static scanSources(input: GateInput, findings: string[]): void {
    for (const file of QualityGate.sourceFiles(input.sources)) {
      QualityGate.read(file).forEach((line, i) => {
        const where = path.basename(file) + ':' + (i + 1);
        if (line.includes('TODO') || line.includes('FIXME')) {
          findings.push('TODO ' + where);
        }
        if (line.includes('console.')) {
          findings.push('console ' + where);
        }
      });
    }
  }

  private static sourceFiles(dir: string): string[] {
    return readdirSync(dir)
      .filter((name) => name.endsWith('.ts'))
      .sort()
      .map((name) => path.join(dir, name));
  }

  private static read(file: string): string[] {
    return readFileSync(file, 'utf8').split(/\r?\n/);
  }
}
