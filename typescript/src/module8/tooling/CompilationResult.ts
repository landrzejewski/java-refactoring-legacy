import { requireNonNull } from '../../shared/requireNonNull.js';
import { unmodifiableSet } from '../UnmodifiableSet.js';
import type { CompilationDiagnostic } from './CompilationDiagnostic.js';
import type { DiagnosticKind } from './Diagnostic.js';

export class CompilationResult {
  readonly successful: boolean;
  readonly diagnostics: readonly CompilationDiagnostic[];
  /** Posortowane nazwy wygenerowanych modułów (odpowiednik TreeSet nazw klas). */
  readonly generatedModuleNames: ReadonlySet<string>;

  constructor(
    successful: boolean,
    diagnostics: readonly CompilationDiagnostic[],
    generatedModuleNames: Iterable<string>,
  ) {
    this.successful = successful;
    this.diagnostics = Object.freeze([...requireNonNull(diagnostics, 'diagnostics')]);
    this.generatedModuleNames = unmodifiableSet(
      [...requireNonNull(generatedModuleNames, 'generatedModuleNames')].sort(),
    );
  }

  hasDiagnostic(kind: DiagnosticKind, code: string): boolean {
    requireNonNull(kind, 'kind');
    requireNonNull(code, 'code');

    return this.diagnostics.some(
      (diagnostic) => diagnostic.kind === kind && diagnostic.code === code,
    );
  }
}
