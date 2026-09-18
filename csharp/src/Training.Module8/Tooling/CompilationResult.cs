using System.Collections.Immutable;
using Microsoft.CodeAnalysis;

namespace Training.Module8.Tooling;

public sealed record CompilationResult
{
    public CompilationResult(
        bool successful,
        IEnumerable<CompilationDiagnostic> diagnostics,
        IEnumerable<string> generatedClassNames)
    {
        ArgumentNullException.ThrowIfNull(diagnostics);
        ArgumentNullException.ThrowIfNull(generatedClassNames);
        Successful = successful;
        Diagnostics = [.. diagnostics];
        GeneratedClassNames = generatedClassNames.ToImmutableSortedSet(
            StringComparer.Ordinal);
    }

    public bool Successful { get; }

    public ImmutableArray<CompilationDiagnostic> Diagnostics { get; }

    public ImmutableSortedSet<string> GeneratedClassNames { get; }

    public bool HasDiagnostic(DiagnosticSeverity kind, string code)
    {
        ArgumentNullException.ThrowIfNull(code);

        return Diagnostics.Any(diagnostic =>
            diagnostic.Kind == kind
            && string.Equals(diagnostic.Code, code, StringComparison.Ordinal));
    }
}
