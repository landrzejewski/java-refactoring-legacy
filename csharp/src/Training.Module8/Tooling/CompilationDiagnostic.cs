using Microsoft.CodeAnalysis;

namespace Training.Module8.Tooling;

/// <summary>
/// Stabilny, niezależny od obiektów kompilatora opis diagnostyki.
/// <see cref="Code"/> równe <c>null</c> oznacza brak kodu specyficznego
/// dla implementacji (odpowiednik <c>Optional.empty()</c> z Javy).
/// </summary>
public sealed record CompilationDiagnostic
{
    /// <summary>Odpowiednik <c>javax.tools.Diagnostic.NOPOS</c>.</summary>
    public const long NoPosition = -1;

    public CompilationDiagnostic(
        DiagnosticSeverity kind,
        string? code,
        long lineNumber,
        long columnNumber)
    {
        if (!Enum.IsDefined(kind))
        {
            throw new ArgumentOutOfRangeException(nameof(kind), kind, null);
        }
        if (code is not null && string.IsNullOrWhiteSpace(code))
        {
            throw new ArgumentException("code must not be blank");
        }
        if (lineNumber != NoPosition && lineNumber < 1)
        {
            throw new ArgumentException(
                "lineNumber must be NoPosition or positive");
        }
        if (columnNumber != NoPosition && columnNumber < 1)
        {
            throw new ArgumentException(
                "columnNumber must be NoPosition or positive");
        }
        Kind = kind;
        Code = code;
        LineNumber = lineNumber;
        ColumnNumber = columnNumber;
    }

    public DiagnosticSeverity Kind { get; }

    public string? Code { get; }

    public long LineNumber { get; }

    public long ColumnNumber { get; }
}
