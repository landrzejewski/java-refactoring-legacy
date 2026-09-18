using Microsoft.CodeAnalysis;
using Training.Module8.Tooling;

namespace Training.Module8.Tests.Tooling;

public sealed class InMemoryCSharpCompilerTest
{
    private const string CleanSource = """
        namespace Example;

        using System.Collections.Generic;

        public sealed class TypedNames
        {
            public int Count(IReadOnlyList<string> names)
            {
                return names.Count;
            }
        }
        """;

    // Odpowiednik surowego typu (raw type) z Javy: kod kompiluje się,
    // ale omija gwarancje systemu typów — dereferencja referencji, która
    // może być null (ostrzeżenie CS8602 w kontekście nullable).
    private const string RawSource = """
        namespace Example;

        using System.Collections.Generic;

        public sealed class RawNames
        {
            public int Count(IReadOnlyList<string>? names)
            {
                return names.Count;
            }
        }
        """;

    private const string NullableDereferenceWarning = "CS8602";

    private readonly InMemoryCSharpCompiler compiler = new();

    [Fact]
    public void CompilesTypedSourceAndKeepsGeneratedBytecodeInMemory()
    {
        CompilationResult result = compiler.Compile(
            "Example.TypedNames",
            CleanSource,
            WarningPolicy.TreatWarningsAsErrors);

        Assert.True(result.Successful);
        Assert.Empty(result.Diagnostics);
        Assert.Equal(["Example.TypedNames"], result.GeneratedClassNames);
    }

    [Fact]
    public void ReportsRawTypeByKindAndCompilerCodeWithoutRejectingSource()
    {
        CompilationResult result = compiler.Compile(
            "Example.RawNames",
            RawSource,
            WarningPolicy.AllowWarnings);

        Assert.True(result.Successful);
        Assert.True(result.HasDiagnostic(
            DiagnosticSeverity.Warning,
            NullableDereferenceWarning));
        Assert.Equal(["Example.RawNames"], result.GeneratedClassNames);
    }

    [Fact]
    public void RejectsTheSameRawTypeWhenWarningsAreErrors()
    {
        CompilationResult result = compiler.Compile(
            "Example.RawNames",
            RawSource,
            WarningPolicy.TreatWarningsAsErrors);

        // Roslyn nie dodaje osobnego błędu "warnings found and -Werror
        // specified" — samo ostrzeżenie jest raportowane jako błąd.
        Assert.False(result.Successful);
        Assert.False(result.HasDiagnostic(
            DiagnosticSeverity.Warning,
            NullableDereferenceWarning));
        Assert.True(result.HasDiagnostic(
            DiagnosticSeverity.Error,
            NullableDereferenceWarning));
        Assert.Empty(result.GeneratedClassNames);
    }

    [Fact]
    public void ValidatesCompilationRequest()
    {
        Assert.Throws<ArgumentNullException>(
            () => compiler.Compile(
                null!, CleanSource, WarningPolicy.AllowWarnings));
        Assert.Throws<ArgumentException>(
            () => compiler.Compile(
                "not a name", CleanSource,
                WarningPolicy.AllowWarnings));
        Assert.Throws<ArgumentException>(
            () => compiler.Compile(
                "Example.class", CleanSource,
                WarningPolicy.AllowWarnings));
        Assert.Throws<ArgumentNullException>(
            () => compiler.Compile(
                "Example.TypedNames", null!,
                WarningPolicy.AllowWarnings));
        Assert.Throws<ArgumentException>(
            () => compiler.Compile(
                "Example.TypedNames", "  \n",
                WarningPolicy.AllowWarnings));
        Assert.Throws<ArgumentOutOfRangeException>(
            () => compiler.Compile(
                "Example.TypedNames", CleanSource, (WarningPolicy)42));
    }

    [Fact]
    public void DiagnosticsCanRepresentAnAbsentImplementationSpecificCode()
    {
        var diagnostic = new CompilationDiagnostic(
            DiagnosticSeverity.Info,
            null,
            CompilationDiagnostic.NoPosition,
            CompilationDiagnostic.NoPosition);

        Assert.Null(diagnostic.Code);
        Assert.Throws<ArgumentException>(
            () => new CompilationDiagnostic(
                DiagnosticSeverity.Info,
                " ",
                CompilationDiagnostic.NoPosition,
                CompilationDiagnostic.NoPosition));
        Assert.Throws<ArgumentException>(
            () => new CompilationDiagnostic(
                DiagnosticSeverity.Info, null, 0, 1));
        Assert.Throws<ArgumentException>(
            () => new CompilationDiagnostic(
                DiagnosticSeverity.Info,
                null,
                CompilationDiagnostic.NoPosition - 1,
                1));
        Assert.Throws<ArgumentException>(
            () => new CompilationDiagnostic(
                DiagnosticSeverity.Info, null, 1, 0));
        Assert.Throws<ArgumentException>(
            () => new CompilationDiagnostic(
                DiagnosticSeverity.Info,
                null,
                1,
                CompilationDiagnostic.NoPosition - 1));
    }
}
