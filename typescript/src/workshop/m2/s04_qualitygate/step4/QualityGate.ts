import { readdirSync, readFileSync } from 'node:fs';
import path from 'node:path';

import ts from 'typescript-api';

import type { GateInput } from '../GateInput.js';

/**
 * Krok 4: + testy zielone - bramka uruchamia przypadki zestawu testów (bez zależności od
 * vitest) i zgłasza porażki. Lista kontrolna jest teraz w całości wykonywalna.
 */
export class QualityGate {
  // Metoda publiczna na poziomie klasy: bez private/protected, poza konstruktorem.
  private static readonly PUBLIC_METHOD = /^ {2}(?:public\s+)?(?:static\s+)?(?!constructor\b)([A-Za-z_]\w*)\s*\(/gm;

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
    QualityGate.runTests(input, findings);
    QualityGate.compileSources(input, findings);
    QualityGate.scanSources(input, findings);
    QualityGate.checkCoverage(input, findings);
    return findings;
  }

  passes(input: GateInput): boolean {
    return this.evaluate(input).length === 0;
  }

  /** Uruchamia przypadki zestawu testów (bez zależności od vitest) i zgłasza porażki. */
  private static runTests(input: GateInput, findings: string[]): void {
    const suite = input.testSuite;
    for (const name of Object.keys(suite.tests).sort()) {
      try {
        suite.tests[name]!();
      } catch (failure) {
        findings.push('test ' + suite.name + '.' + name + ' nie przechodzi: ' + (failure as Error).name);
      }
    }
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

  /** Przybliżenie pokrycia: publiczna metoda kluczowej klasy musi być wywołana w jej teście. */
  private static checkCoverage(input: GateInput, findings: string[]): void {
    const source = readFileSync(path.join(input.sources, input.keyClass + '.ts'), 'utf8');
    const test = readFileSync(input.testSource, 'utf8');
    for (const method of source.matchAll(QualityGate.PUBLIC_METHOD)) {
      if (!test.includes('.' + method[1]! + '(')) {
        findings.push('pokrycie ' + input.keyClass + '.' + method[1]! + ' bez testu');
      }
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
