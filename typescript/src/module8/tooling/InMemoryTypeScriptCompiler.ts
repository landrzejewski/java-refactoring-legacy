import { readFileSync } from 'node:fs';
import path from 'node:path';
import ts from 'typescript-api';
import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import { CompilationDiagnostic } from './CompilationDiagnostic.js';
import { CompilationResult } from './CompilationResult.js';
import { DiagnosticKind, NOPOS } from './Diagnostic.js';
import { WarningPolicy } from './WarningPolicy.js';

/**
 * Odpowiednik InMemoryJavaCompiler: kompiluje jeden plik źródłowy przez API
 * kompilatora TypeScript (pakiet `typescript-api` = TypeScript 5.9), nie
 * dotykając dysku poza odczytem plików bibliotecznych `lib.*.d.ts`.
 *
 * Odpowiednikiem ostrzeżenia javac `-Xlint:rawtypes` (surowy typ bez
 * parametrów) jest niejawny typ `any` (TS7006 i pokrewne) — kompilator
 * zgłasza go jako błąd w trybie strict, a my raportujemy go jako WARNING.
 * Polityka TREAT_WARNINGS_AS_ERRORS działa jak `-Werror`.
 */
export class InMemoryTypeScriptCompiler {
  /** Kod diagnostyki dodawanej, gdy ostrzeżenia są traktowane jak błędy. */
  static readonly WARNINGS_AS_ERRORS_CODE = 'WARNINGS_AS_ERRORS';

  private static readonly SOURCE_ROOT = '/memory';

  // Diagnostyki niejawnego `any` — „lint” odpowiadający -Xlint:rawtypes.
  private static readonly LINT_WARNING_CODES: ReadonlySet<number> = new Set([
    7005, 7006, 7008, 7010, 7011, 7019, 7031, 7034,
  ]);

  private static readonly BASE_OPTIONS: ts.CompilerOptions = {
    target: ts.ScriptTarget.ES2023,
    module: ts.ModuleKind.ES2022,
    lib: ['lib.es2023.d.ts'],
    types: [],
    strict: true,
    skipLibCheck: true,
    newLine: ts.NewLineKind.LineFeed,
  };

  // Pliki lib.*.d.ts są niezmienne — parsujemy je raz na proces.
  private static readonly libraryFiles = new Map<string, ts.SourceFile>();

  compile(moduleName: string, source: string, warningPolicy: WarningPolicy): CompilationResult {
    InMemoryTypeScriptCompiler.validateModuleName(moduleName);
    requireNonNull(source, 'source');
    requireNonNull(warningPolicy, 'warningPolicy');
    if (source.trim().length === 0) {
      throw new IllegalArgumentError('source must not be blank');
    }

    const options = InMemoryTypeScriptCompiler.BASE_OPTIONS;
    const sourcePath = InMemoryTypeScriptCompiler.pathFor(moduleName, '.ts');
    const sourceFile = ts.createSourceFile(sourcePath, source, options.target ?? ts.ScriptTarget.Latest);
    const generatedModules = new Map<string, string>();
    const host = InMemoryTypeScriptCompiler.createHost(sourceFile, generatedModules);
    const program = ts.createProgram({ rootNames: [sourcePath], options, host });

    const diagnostics = ts
      .getPreEmitDiagnostics(program)
      .map((diagnostic) => InMemoryTypeScriptCompiler.toStable(diagnostic));
    if (
      warningPolicy === WarningPolicy.TREAT_WARNINGS_AS_ERRORS &&
      diagnostics.some((diagnostic) => diagnostic.kind === DiagnosticKind.WARNING)
    ) {
      diagnostics.push(
        new CompilationDiagnostic(
          DiagnosticKind.ERROR,
          InMemoryTypeScriptCompiler.WARNINGS_AS_ERRORS_CODE,
          NOPOS,
          NOPOS,
        ),
      );
    }

    const successful = !diagnostics.some((diagnostic) => diagnostic.kind === DiagnosticKind.ERROR);
    if (successful) {
      // Jak javac: kod wynikowy powstaje tylko dla udanej kompilacji.
      program.emit();
    }
    return new CompilationResult(successful, diagnostics, generatedModules.keys());
  }

  private static createHost(
    sourceFile: ts.SourceFile,
    generatedModules: Map<string, string>,
  ): ts.CompilerHost {
    const libraryDirectory = path.dirname(
      ts.getDefaultLibFilePath(InMemoryTypeScriptCompiler.BASE_OPTIONS),
    );
    const isLibraryFile = (fileName: string): boolean =>
      path.dirname(fileName) === libraryDirectory;

    return {
      getSourceFile: (fileName, languageVersion) => {
        if (fileName === sourceFile.fileName) {
          return sourceFile;
        }
        if (!isLibraryFile(fileName)) {
          return undefined;
        }
        let library = InMemoryTypeScriptCompiler.libraryFiles.get(fileName);
        if (library === undefined) {
          library = ts.createSourceFile(fileName, readFileSync(fileName, 'utf8'), languageVersion);
          InMemoryTypeScriptCompiler.libraryFiles.set(fileName, library);
        }
        return library;
      },
      getDefaultLibFileName: (options) =>
        path.join(libraryDirectory, ts.getDefaultLibFileName(options)),
      getDefaultLibLocation: () => libraryDirectory,
      writeFile: (fileName, text) => {
        if (fileName.endsWith('.js')) {
          generatedModules.set(InMemoryTypeScriptCompiler.moduleNameFor(fileName), text);
        }
      },
      getCurrentDirectory: () => InMemoryTypeScriptCompiler.SOURCE_ROOT,
      getDirectories: () => [],
      fileExists: (fileName) =>
        fileName === sourceFile.fileName || (isLibraryFile(fileName) && ts.sys.fileExists(fileName)),
      readFile: (fileName) =>
        fileName === sourceFile.fileName
          ? sourceFile.text
          : isLibraryFile(fileName)
            ? ts.sys.readFile(fileName)
            : undefined,
      getCanonicalFileName: (fileName) => fileName,
      useCaseSensitiveFileNames: () => true,
      getNewLine: () => '\n',
    };
  }

  private static toStable(diagnostic: ts.Diagnostic): CompilationDiagnostic {
    let lineNumber = NOPOS;
    let columnNumber = NOPOS;
    if (diagnostic.file !== undefined && diagnostic.start !== undefined) {
      const position = diagnostic.file.getLineAndCharacterOfPosition(diagnostic.start);
      lineNumber = position.line + 1;
      columnNumber = position.character + 1;
    }
    return new CompilationDiagnostic(
      InMemoryTypeScriptCompiler.kindOf(diagnostic),
      `TS${diagnostic.code}`,
      lineNumber,
      columnNumber,
    );
  }

  private static kindOf(diagnostic: ts.Diagnostic): DiagnosticKind {
    if (InMemoryTypeScriptCompiler.LINT_WARNING_CODES.has(diagnostic.code)) {
      return DiagnosticKind.WARNING;
    }
    switch (diagnostic.category) {
      case ts.DiagnosticCategory.Error:
        return DiagnosticKind.ERROR;
      case ts.DiagnosticCategory.Warning:
        return DiagnosticKind.WARNING;
      case ts.DiagnosticCategory.Suggestion:
      case ts.DiagnosticCategory.Message:
        return DiagnosticKind.NOTE;
      default:
        return DiagnosticKind.OTHER;
    }
  }

  /** Odpowiednik SourceVersion.isName: kropkowana nazwa z poprawnych identyfikatorów. */
  private static validateModuleName(moduleName: string): void {
    requireNonNull(moduleName, 'moduleName');
    const scanner = ts.createScanner(ts.ScriptTarget.Latest, false);
    const valid = moduleName.split('.').every((part) => {
      scanner.setText(part);
      const token = scanner.scan();
      return (
        (token === ts.SyntaxKind.Identifier ||
          (token >= ts.SyntaxKind.FirstKeyword && token <= ts.SyntaxKind.LastKeyword)) &&
        !scanner.isReservedWord() &&
        scanner.getTokenEnd() === part.length
      );
    });
    if (!valid) {
      throw new IllegalArgumentError('moduleName must be a valid dotted module name');
    }
  }

  private static pathFor(moduleName: string, extension: string): string {
    return `${InMemoryTypeScriptCompiler.SOURCE_ROOT}/${moduleName.replaceAll('.', '/')}${extension}`;
  }

  private static moduleNameFor(outputPath: string): string {
    const relative = path.posix.relative(InMemoryTypeScriptCompiler.SOURCE_ROOT, outputPath);
    return relative.replace(/\.js$/, '').replaceAll('/', '.');
  }
}
