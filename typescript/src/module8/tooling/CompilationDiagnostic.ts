import { IllegalArgumentError, NullPointerError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import { NOPOS, type DiagnosticKind } from './Diagnostic.js';

export class CompilationDiagnostic {
  readonly kind: DiagnosticKind;
  /** Optional<String> z Javy: `undefined` = brak kodu, `null` jest błędem. */
  readonly code: string | undefined;
  readonly lineNumber: number;
  readonly columnNumber: number;

  constructor(
    kind: DiagnosticKind,
    code: string | undefined,
    lineNumber: number,
    columnNumber: number,
  ) {
    this.kind = requireNonNull(kind, 'kind');
    if (code === null) {
      throw new NullPointerError('code');
    }
    if (code !== undefined && code.trim().length === 0) {
      throw new IllegalArgumentError('code must not be blank');
    }
    if (lineNumber !== NOPOS && lineNumber < 1) {
      throw new IllegalArgumentError('lineNumber must be NOPOS or positive');
    }
    if (columnNumber !== NOPOS && columnNumber < 1) {
      throw new IllegalArgumentError('columnNumber must be NOPOS or positive');
    }
    this.code = code;
    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
  }
}
