import { readdirSync, readFileSync } from 'node:fs';
import path from 'node:path';

import ts from 'typescript-api';

import type { GateInput } from '../GateInput.js';

/**
 * Krok 2: + diagnostyka kompilatora (typescript-api, strict + noUnused*). Każda diagnostyka
 * to wynik z kodem i pozycją; bramka nie przepuszcza nowych ostrzeżeń w domenie.
 */
export class QualityGate {
  private static readonly COMPILER_OPTIONS: ts.CompilerOptions = {
    target: ts.ScriptTarget.ES2023,
    module: ts.ModuleKind.NodeNext,
    moduleResolution: ts.ModuleResolutionKind.NodeNext,
    lib: ['lib.es2023.d.ts', 'lib.dom.d.ts'],
    types: [],
    strict: true,
    noUncheckedIndexedAccess: true,
    exactOptionalPropertyTypes: true,
    noImplicitOverride: true,
    noFallthroughCasesInSwitch: true,
    noUnusedLocals: true,
    noUnusedParameters: true,
    noImplicitReturns: true,
    skipLibCheck: true,
    noEmit: true,
  };

  /** Lista wyników bramki; pusta lista = bramka przepuszcza zmianę. */
  evaluate(input: GateInput): string[] {
    const findings: string[] = [];
    QualityGate.compileSources(input, findings);
    QualityGate.scanSources(input, findings);
    return findings;
  }

  passes(input: GateInput): boolean {
    return this.evaluate(input).length === 0;
  }

  /** Kompilacja źródeł domeny (typescript-api, strict i opcje "lint"); każda diagnostyka to wynik bramki. */
  private static compileSources(input: GateInput, findings: string[]): void {
    const program = ts.createProgram(QualityGate.sourceFiles(input.sources), QualityGate.COMPILER_OPTIONS);
    for (const sourceFile of program.getRootFileNames().map((name) => program.getSourceFile(name))) {
      if (sourceFile === undefined) {
        continue;
      }
      for (const d of [...program.getSyntacticDiagnostics(sourceFile), ...program.getSemanticDiagnostics(sourceFile)]) {
        const line = sourceFile.getLineAndCharacterOfPosition(d.start ?? 0).line + 1;
        findings.push('kompilator ' + path.basename(sourceFile.fileName) + ':' + line + ' TS' + d.code);
      }
    }
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
