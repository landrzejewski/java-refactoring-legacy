using System.Collections.Immutable;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.Emit;

namespace Training.Workshop.M8.S08CompilerGate;

/// <summary>
/// Bramka kompilatora: kompiluje wszystkie pliki katalogu w pamięci przez Roslyn (odpowiednik
/// javax.tools) z włączonym kontekstem nullable, wszystkimi falami ostrzeżeń (odpowiednik
/// -Xlint:all) i ostrzeżeniami traktowanymi jako błędy (odpowiednik -Werror). Zwraca werdykt
/// oraz strukturalną diagnostykę (identyfikator ostrzeżenia, plik, linia) zamiast tekstu z konsoli.
/// <para>
/// W odróżnieniu od javac Roslyn z ostrzeżeniami jako błędami nie przerywa analizy po pierwszej
/// fazie - jeden przebieg daje i werdykt, i pełną listę ostrzeżeń.
/// </para>
/// </summary>
public static class CompilerGate
{
    /// <summary>Opcje bramki: biblioteka, nullable, wszystkie fale ostrzeżeń, ostrzeżenia jako błędy.</summary>
    public static readonly CSharpCompilationOptions Options = new(
        OutputKind.DynamicallyLinkedLibrary,
        nullableContextOptions: NullableContextOptions.Enable,
        warningLevel: 9999,
        generalDiagnosticOption: ReportDiagnostic.Error);

    /// <summary>Te same globalne dyrektywy using, które SDK dodaje przy ImplicitUsings (jak w projekcie).</summary>
    private const string ImplicitUsings = """
        global using System;
        global using System.Collections.Generic;
        global using System.IO;
        global using System.Linq;
        global using System.Net.Http;
        global using System.Threading;
        global using System.Threading.Tasks;
        """;

    private static readonly CSharpParseOptions ParseOptions = new(LanguageVersion.Latest);

    private static readonly Lazy<ImmutableArray<MetadataReference>> References = new(LoadPlatformReferences);

    /// <summary>Ostrzeżenie kompilatora: identyfikator Roslyn (np. CS8602), plik i linia.</summary>
    public sealed record Warning(string Category, string File, long Line)
    {
        public override string ToString()
        {
            return "[" + Category + "] " + File + ":" + Line;
        }
    }

    public sealed record Result(bool Passed, IReadOnlyList<Warning> Warnings)
    {
        public IReadOnlyList<string> Categories()
        {
            return Warnings.Select(warning => warning.Category).Distinct().Order(StringComparer.Ordinal).ToList();
        }
    }

    public static Result Check(string sourceDir)
    {
        List<SyntaxTree> trees = CSharpFiles(sourceDir)
            .Select(file => CSharpSyntaxTree.ParseText(File.ReadAllText(file), ParseOptions, path: file))
            .ToList();
        trees.Add(CSharpSyntaxTree.ParseText(ImplicitUsings, ParseOptions, path: "GlobalUsings.g.cs"));
        CSharpCompilation compilation = CSharpCompilation.Create(
            "CompilerGate", trees, References.Value, Options);
        using var output = new MemoryStream();
        EmitResult emit = compilation.Emit(output);
        return new Result(emit.Success, Warnings(emit.Diagnostics));
    }

    private static IReadOnlyList<Warning> Warnings(ImmutableArray<Diagnostic> diagnostics)
    {
        return diagnostics
            .Where(d => d.IsWarningAsError && d.Location.IsInSource)
            .Select(d => new Warning(
                d.Id,
                Path.GetFileName(d.Location.SourceTree!.FilePath),
                d.Location.GetLineSpan().StartLinePosition.Line + 1))
            .OrderBy(w => w.File, StringComparer.Ordinal)
            .ThenBy(w => w.Line)
            .ThenBy(w => w.Category, StringComparer.Ordinal)
            .ToList();
    }

    private static IReadOnlyList<string> CSharpFiles(string dir)
    {
        return Directory.GetFiles(dir)
            .Where(f => f.EndsWith(".cs", StringComparison.Ordinal))
            .Order(StringComparer.Ordinal)
            .ToList();
    }

    private static ImmutableArray<MetadataReference> LoadPlatformReferences()
    {
        string trustedAssemblies = AppContext.GetData("TRUSTED_PLATFORM_ASSEMBLIES") as string
            ?? throw new InvalidOperationException("brak bibliotek platformy - uruchom na .NET");
        return
        [
            .. trustedAssemblies
                .Split(Path.PathSeparator, StringSplitOptions.RemoveEmptyEntries)
                .Where(IsFrameworkAssembly)
                .Select(path => MetadataReference.CreateFromFile(path)),
        ];
    }

    private static bool IsFrameworkAssembly(string path)
    {
        string fileName = Path.GetFileName(path);
        return fileName.StartsWith("System.", StringComparison.Ordinal)
            || fileName is "mscorlib.dll" or "netstandard.dll";
    }
}
