import { describe, expect, it } from 'vitest';
import { IllegalArgumentError, NullPointerError } from '../../../src/shared/errors.js';
import { CompilationDiagnostic } from '../../../src/module8/tooling/CompilationDiagnostic.js';
import { DiagnosticKind, NOPOS } from '../../../src/module8/tooling/Diagnostic.js';
import { InMemoryTypeScriptCompiler } from '../../../src/module8/tooling/InMemoryTypeScriptCompiler.js';
import { WarningPolicy } from '../../../src/module8/tooling/WarningPolicy.js';

// Odpowiednik List<?> — parametr ma jawny typ.
const CLEAN_SOURCE = `export class TypedNames {
    count(names: readonly unknown[]): number {
        return names.length;
    }
}
`;

// Odpowiednik surowego typu List — parametr bez typu (niejawny any).
const RAW_SOURCE = `export class RawNames {
    count(names) {
        return names.length;
    }
}
`;

// Kod TS dla „Parameter implicitly has an 'any' type” (odpowiednik compiler.warn.raw.class.use).
const IMPLICIT_ANY_CODE = 'TS7006';

describe('InMemoryJavaCompilerTest', () => {
  const compiler = new InMemoryTypeScriptCompiler();

  it('compilesTypedSourceAndKeepsGeneratedBytecodeInMemory', () => {
    const result = compiler.compile(
      'example.TypedNames',
      CLEAN_SOURCE,
      WarningPolicy.TREAT_WARNINGS_AS_ERRORS,
    );

    expect(result.successful).toBe(true);
    expect(result.diagnostics).toEqual([]);
    expect(new Set(result.generatedModuleNames)).toEqual(new Set(['example.TypedNames']));
  });

  it('reportsRawTypeByKindAndCompilerCodeWithoutRejectingSource', () => {
    const result = compiler.compile('example.RawNames', RAW_SOURCE, WarningPolicy.ALLOW_WARNINGS);

    expect(result.successful).toBe(true);
    expect(result.hasDiagnostic(DiagnosticKind.WARNING, IMPLICIT_ANY_CODE)).toBe(true);
    expect(new Set(result.generatedModuleNames)).toEqual(new Set(['example.RawNames']));
  });

  it('rejectsTheSameRawTypeWhenWarningsAreErrors', () => {
    const result = compiler.compile(
      'example.RawNames',
      RAW_SOURCE,
      WarningPolicy.TREAT_WARNINGS_AS_ERRORS,
    );

    expect(result.successful).toBe(false);
    expect(result.hasDiagnostic(DiagnosticKind.WARNING, IMPLICIT_ANY_CODE)).toBe(true);
    expect(
      result.hasDiagnostic(DiagnosticKind.ERROR, InMemoryTypeScriptCompiler.WARNINGS_AS_ERRORS_CODE),
    ).toBe(true);
  });

  it('validatesCompilationRequest', () => {
    expect(() =>
      compiler.compile(null as unknown as string, CLEAN_SOURCE, WarningPolicy.ALLOW_WARNINGS),
    ).toThrow(NullPointerError);
    expect(() =>
      compiler.compile('not a name', CLEAN_SOURCE, WarningPolicy.ALLOW_WARNINGS),
    ).toThrow(IllegalArgumentError);
    expect(() =>
      compiler.compile('example.TypedNames', null as unknown as string, WarningPolicy.ALLOW_WARNINGS),
    ).toThrow(NullPointerError);
    expect(() =>
      compiler.compile('example.TypedNames', '  \n', WarningPolicy.ALLOW_WARNINGS),
    ).toThrow(IllegalArgumentError);
    expect(() =>
      compiler.compile('example.TypedNames', CLEAN_SOURCE, null as unknown as WarningPolicy),
    ).toThrow(NullPointerError);
  });

  it('diagnosticsCanRepresentAnAbsentImplementationSpecificCode', () => {
    const diagnostic = new CompilationDiagnostic(DiagnosticKind.NOTE, undefined, NOPOS, NOPOS);

    expect(diagnostic.code).toBeUndefined();
    expect(
      () =>
        new CompilationDiagnostic(DiagnosticKind.NOTE, null as unknown as undefined, NOPOS, NOPOS),
    ).toThrow(NullPointerError);
    expect(() => new CompilationDiagnostic(DiagnosticKind.NOTE, ' ', NOPOS, NOPOS)).toThrow(
      IllegalArgumentError,
    );
    expect(() => new CompilationDiagnostic(DiagnosticKind.NOTE, undefined, 0, 1)).toThrow(
      IllegalArgumentError,
    );
    expect(() => new CompilationDiagnostic(DiagnosticKind.NOTE, undefined, NOPOS - 1, 1)).toThrow(
      IllegalArgumentError,
    );
    expect(() => new CompilationDiagnostic(DiagnosticKind.NOTE, undefined, 1, 0)).toThrow(
      IllegalArgumentError,
    );
    expect(() => new CompilationDiagnostic(DiagnosticKind.NOTE, undefined, 1, NOPOS - 1)).toThrow(
      IllegalArgumentError,
    );
  });
});
