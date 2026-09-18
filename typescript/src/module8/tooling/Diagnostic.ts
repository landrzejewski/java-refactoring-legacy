// Odpowiednik javax.tools.Diagnostic: rodzaj diagnostyki i znacznik braku pozycji.

export enum DiagnosticKind {
  ERROR = 'ERROR',
  WARNING = 'WARNING',
  MANDATORY_WARNING = 'MANDATORY_WARNING',
  NOTE = 'NOTE',
  OTHER = 'OTHER',
}

/** Odpowiednik Diagnostic.NOPOS — pozycja nieznana. */
export const NOPOS = -1;
