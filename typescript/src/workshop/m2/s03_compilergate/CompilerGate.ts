import { readdirSync } from 'node:fs';
import path from 'node:path';

import ts from 'typescript-api';

/** Ostrzeżenie bramki: kategoria (jak -Xlint w javac), plik i linia. */
export class Warning {
  constructor(readonly category: string, readonly file: string, readonly line: number) {}

  toString(): string {
    return `[${this.category}] ${this.file}:${this.line}`;
  }
}

/** Werdykt bramki i pełna lista ostrzeżeń. */
export class Result {
  constructor(readonly passed: boolean, readonly warnings: readonly Warning[]) {}

  categories(): string[] {
    return [...new Set(this.warnings.map((warning) => warning.category))].sort();
  }
}

/**
 * Bramka kompilatora: kompiluje w pamięci wszystkie pliki katalogu przez API kompilatora
 * TypeScript (pakiet `typescript-api` = TypeScript 5.9) z opcjami ostrzejszymi niż build
 * projektu i zwraca werdykt oraz strukturalną diagnostykę (kategoria, plik, linia) zamiast
 * tekstu z konsoli. Każde ostrzeżenie jest traktowane jak błąd (odpowiednik -Werror).
 *
 * Kategorie (odpowiedniki -Xlint:all):
 * - rawtypes - typ generyczny utworzony bez argumentów typu, które TS uzupełnił jako any (new Map()),
 * - unchecked - niesprawdzona asercja typu (as) na wartości typu any,
 * - deprecation - wywołanie sygnatury oznaczonej w JSDoc jako @deprecated,
 * - fallthrough - przelot między case w switch (noFallthroughCasesInSwitch),
 * - unused, implicitReturns, unreachable, indexSignature - pozostałe opcje bramki.
 *
 * Uwaga: bramka nie honoruje dyrektyw @ts-expect-error i @ts-ignore - wyciszenie w kodzie
 * nie przepuszcza ostrzeżenia przez bramkę (build projektu je honoruje).
 */
export class CompilerGate {
  static readonly OPTIONS: Readonly<ts.CompilerOptions> = {
    target: ts.ScriptTarget.ES2023,
    module: ts.ModuleKind.NodeNext,
    moduleResolution: ts.ModuleResolutionKind.NodeNext,
    lib: ['lib.es2023.d.ts'],
    types: [],
    strict: true,
    noUncheckedIndexedAccess: true,
    exactOptionalPropertyTypes: true,
    noImplicitOverride: true,
    noFallthroughCasesInSwitch: true,
    noUnusedLocals: true,
    noUnusedParameters: true,
    noImplicitReturns: true,
    noPropertyAccessFromIndexSignature: true,
    allowUnreachableCode: false,
    allowUnusedLabels: false,
    skipLibCheck: true,
    noEmit: true,
  };

  private static readonly SUPPRESSION = /@ts-(?:expect-error|ignore|nocheck)/g;

  private constructor() {}

  static check(sourceDir: string): Result {
    const sources = CompilerGate.sourceFiles(sourceDir);
    const program = ts.createProgram({
      rootNames: sources,
      options: CompilerGate.OPTIONS,
      host: CompilerGate.hostWithoutSuppressions(),
    });
    const warnings: Warning[] = [];
    const checker = program.getTypeChecker();
    for (const fileName of sources) {
      const sourceFile = program.getSourceFile(fileName);
      if (sourceFile === undefined) {
        continue;
      }
      const diagnostics = [
        ...program.getSyntacticDiagnostics(sourceFile),
        ...program.getSemanticDiagnostics(sourceFile),
      ];
      for (const diagnostic of diagnostics) {
        warnings.push(CompilerGate.warning(CompilerGate.category(diagnostic.code), sourceFile, diagnostic.start ?? 0));
      }
      warnings.push(...CompilerGate.lint(sourceFile, checker));
    }
    warnings.sort((a, b) => a.file.localeCompare(b.file) || a.line - b.line || a.category.localeCompare(b.category));
    return new Result(warnings.length === 0, warnings);
  }

  /** Host czytający pliki z dysku, ale z "rozbrojonymi" dyrektywami wyciszającymi. */
  private static hostWithoutSuppressions(): ts.CompilerHost {
    const host = ts.createCompilerHost(CompilerGate.OPTIONS);
    const original = host.getSourceFile.bind(host);
    host.getSourceFile = (fileName, languageVersion, onError, shouldCreate) => {
      if (fileName.includes(`${path.sep}lib.`) || fileName.endsWith('.d.ts')) {
        return original(fileName, languageVersion, onError, shouldCreate);
      }
      const text = host.readFile(fileName);
      return text === undefined
        ? undefined
        : ts.createSourceFile(fileName, text.replace(CompilerGate.SUPPRESSION, 'ts-gate-ignored'), languageVersion);
    };
    return host;
  }

  /** Ostrzeżenia, których kompilator sam nie zgłasza: rawtypes, unchecked, deprecation. */
  private static lint(sourceFile: ts.SourceFile, checker: ts.TypeChecker): Warning[] {
    const warnings: Warning[] = [];
    const visit = (node: ts.Node): void => {
      if (ts.isNewExpression(node) && node.typeArguments === undefined && CompilerGate.hasAnyTypeArgument(node, checker)) {
        warnings.push(CompilerGate.warning('rawtypes', sourceFile, node.getStart()));
      }
      if (ts.isAsExpression(node) && (checker.getTypeAtLocation(node.expression).flags & ts.TypeFlags.Any) !== 0) {
        warnings.push(CompilerGate.warning('unchecked', sourceFile, node.getStart()));
      }
      if (ts.isCallExpression(node) || ts.isNewExpression(node)) {
        const signature = checker.getResolvedSignature(node);
        if (signature?.getJsDocTags().some((tag) => tag.name === 'deprecated')) {
          warnings.push(CompilerGate.warning('deprecation', sourceFile, node.getStart()));
        }
      }
      ts.forEachChild(node, visit);
    };
    visit(sourceFile);
    return warnings;
  }

  private static hasAnyTypeArgument(node: ts.NewExpression, checker: ts.TypeChecker): boolean {
    const type = checker.getTypeAtLocation(node);
    if ((type.flags & ts.TypeFlags.Object) === 0
      || ((type as ts.ObjectType).objectFlags & ts.ObjectFlags.Reference) === 0) {
      return false;
    }
    return checker.getTypeArguments(type as ts.TypeReference).some((argument) => (argument.flags & ts.TypeFlags.Any) !== 0);
  }

  /** Kod diagnostyki TypeScript na nazwę kategorii bramki. */
  private static category(code: number): string {
    switch (code) {
      case 7029: return 'fallthrough';
      case 6133: case 6138: case 6192: case 6196: case 6198: return 'unused';
      case 7030: return 'implicitReturns';
      case 7027: return 'unreachable';
      case 7028: return 'unusedLabel';
      case 4111: return 'indexSignature';
      default: return `TS${code}`;
    }
  }

  private static warning(category: string, sourceFile: ts.SourceFile, position: number): Warning {
    const line = sourceFile.getLineAndCharacterOfPosition(position).line + 1;
    return new Warning(category, path.basename(sourceFile.fileName), line);
  }

  private static sourceFiles(dir: string): string[] {
    return readdirSync(dir)
      .filter((name) => name.endsWith('.ts'))
      .sort()
      .map((name) => path.join(dir, name));
  }
}
