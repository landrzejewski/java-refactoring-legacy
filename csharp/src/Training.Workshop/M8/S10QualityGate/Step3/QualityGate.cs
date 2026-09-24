using System.Collections.Immutable;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using System.Text.RegularExpressions;

namespace Training.Workshop.M8.S10QualityGate.Step3;

/// <summary>
/// Krok 3: + pokrycie kluczowej klasy - przybliżenie: każda publiczna metoda musi być wywołana
/// w jej teście. To nie zastępuje pomiaru pokrycia (coverlet), ale łapie metodę bez żadnego testu.
/// </summary>
public sealed class QualityGate
{
    private static readonly Regex PublicMethod =
        new(@"public\s+(?:static\s+)?[\w<>\[\],?]+\s+(\w+)\s*\(");

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

    private static readonly Lazy<ImmutableArray<MetadataReference>> PlatformReferences = new(LoadPlatformReferences);

    /// <summary>Lista wyników bramki; pusta lista = bramka przepuszcza zmianę.</summary>
    public IReadOnlyList<string> Evaluate(GateInput input)
    {
        var findings = new List<string>();
        CompileSources(input, findings);
        ScanSources(input, findings);
        CheckCoverage(input, findings);
        return findings;
    }

    public bool Passes(GateInput input)
    {
        return Evaluate(input).Count == 0;
    }

    /// <summary>Kompilacja źródeł domeny (nullable, wszystkie fale ostrzeżeń); każde ostrzeżenie to wynik bramki.</summary>
    private static void CompileSources(GateInput input, List<string> findings)
    {
        CSharpCompilation compilation = Compile(CSharpFiles(input.Sources), PlatformReferences.Value);
        IEnumerable<Diagnostic> problems = compilation.GetDiagnostics()
            .Where(d => d.Severity is DiagnosticSeverity.Warning or DiagnosticSeverity.Error && d.Location.IsInSource)
            .OrderBy(d => d.Location.SourceTree!.FilePath, StringComparer.Ordinal)
            .ThenBy(d => d.Location.SourceSpan.Start)
            .ThenBy(d => d.Id, StringComparer.Ordinal);
        foreach (Diagnostic d in problems)
        {
            findings.Add("kompilator " + Path.GetFileName(d.Location.SourceTree!.FilePath) + ":"
                + (d.Location.GetLineSpan().StartLinePosition.Line + 1) + " " + d.Id);
        }
    }

    private static CSharpCompilation Compile(IEnumerable<string> files, IEnumerable<MetadataReference> references)
    {
        List<SyntaxTree> trees = files
            .Select(file => CSharpSyntaxTree.ParseText(File.ReadAllText(file), ParseOptions, path: file))
            .ToList();
        trees.Add(CSharpSyntaxTree.ParseText(ImplicitUsings, ParseOptions, path: "GlobalUsings.g.cs"));
        return CSharpCompilation.Create(
            "QualityGate",
            trees,
            references,
            new CSharpCompilationOptions(
                OutputKind.DynamicallyLinkedLibrary,
                nullableContextOptions: NullableContextOptions.Enable,
                warningLevel: 9999));
    }

    private static ImmutableArray<MetadataReference> LoadPlatformReferences()
    {
        string trustedAssemblies = AppContext.GetData("TRUSTED_PLATFORM_ASSEMBLIES") as string
            ?? throw new InvalidOperationException("brak bibliotek platformy - uruchom na .NET");
        return
        [
            .. trustedAssemblies
                .Split(Path.PathSeparator, StringSplitOptions.RemoveEmptyEntries)
                .Where(path => Path.GetFileName(path).StartsWith("System.", StringComparison.Ordinal)
                    || Path.GetFileName(path) is "mscorlib.dll" or "netstandard.dll")
                .Select(path => MetadataReference.CreateFromFile(path)),
        ];
    }

    /// <summary>TODO/FIXME i wydruki na konsolę w źródłach domeny.</summary>
    private static void ScanSources(GateInput input, List<string> findings)
    {
        foreach (string file in CSharpFiles(input.Sources))
        {
            string[] lines = File.ReadAllLines(file);
            for (int i = 0; i < lines.Length; i++)
            {
                string line = lines[i];
                string where = Path.GetFileName(file) + ":" + (i + 1);
                if (line.Contains("TODO") || line.Contains("FIXME"))
                {
                    findings.Add("TODO " + where);
                }
                if (line.Contains("Console.Write") || line.Contains("Console.Error"))
                {
                    findings.Add("Console " + where);
                }
            }
        }
    }

    /// <summary>Przybliżenie pokrycia: publiczna metoda kluczowej klasy musi być wywołana w jej teście.</summary>
    private static void CheckCoverage(GateInput input, List<string> findings)
    {
        string source = File.ReadAllText(Path.Combine(input.Sources, input.KeyClass + ".cs"));
        string test = File.ReadAllText(input.TestSource);
        foreach (Match method in PublicMethod.Matches(source))
        {
            if (!test.Contains("." + method.Groups[1].Value + "("))
            {
                findings.Add("pokrycie " + input.KeyClass + "." + method.Groups[1].Value + " bez testu");
            }
        }
    }

    private static IReadOnlyList<string> CSharpFiles(string dir)
    {
        return Directory.GetFiles(dir)
            .Where(f => f.EndsWith(".cs", StringComparison.Ordinal))
            .Order(StringComparer.Ordinal)
            .ToList();
    }
}
