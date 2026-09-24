using System.Collections.Immutable;
using System.Reflection;
using System.Runtime.Loader;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M5.Tooling;

/// <summary>
/// Kompilacja kodu C# w pamięci (Roslyn) - odpowiednik javax.tools w testach modułu 5.
/// Każde assembly ma referencje do platformy .NET i do assembly warsztatu (Money, kontrakty scen).
/// </summary>
internal sealed record InMemoryAssembly(string Name, bool Success, byte[] Image, ImmutableArray<Diagnostic> Errors)
{
    private static readonly Lazy<ImmutableArray<MetadataReference>> Platform = new(LoadPlatform);

    /// <summary>Te same globalne using co ImplicitUsings w projektach warsztatu.</summary>
    private const string ImplicitUsings = """
        global using System;
        global using System.Collections.Generic;
        global using System.IO;
        global using System.Linq;
        global using System.Threading;
        global using System.Threading.Tasks;
        """;

    public MetadataReference Reference => MetadataReference.CreateFromImage(Image);

    public string ErrorIds => string.Join(", ", Errors.Select(e => e.Id).Distinct());

    public static InMemoryAssembly Compile(string name, IEnumerable<string> sources, params InMemoryAssembly[] references)
    {
        var compilation = CSharpCompilation.Create(
            name,
            sources.Append(ImplicitUsings).Select(source => CSharpSyntaxTree.ParseText(source, new CSharpParseOptions(LanguageVersion.Latest))),
            [.. Platform.Value, .. references.Select(r => r.Reference)],
            new CSharpCompilationOptions(
                OutputKind.DynamicallyLinkedLibrary,
                nullableContextOptions: NullableContextOptions.Enable));
        using var image = new MemoryStream();
        var result = compilation.Emit(image);
        var errors = result.Diagnostics.Where(d => d.Severity == DiagnosticSeverity.Error).ToImmutableArray();
        return new InMemoryAssembly(name, result.Success, image.ToArray(), errors);
    }

    /// <summary>
    /// Ładuje zestaw assembly w osobnym kontekście - jak podmiana plików DLL na serwerze.
    /// Nazwy spoza zestawu (platforma, assembly warsztatu) rozwiązuje kontekst domyślny.
    /// </summary>
    public static Assembly Load(InMemoryAssembly main, params InMemoryAssembly[] others)
    {
        var context = new Context([main, .. others]);
        return context.LoadFromAssemblyName(new AssemblyName(main.Name));
    }

    private static ImmutableArray<MetadataReference> LoadPlatform()
    {
        var trusted = (string)AppContext.GetData("TRUSTED_PLATFORM_ASSEMBLIES")!;
        return
        [
            .. trusted.Split(Path.PathSeparator, StringSplitOptions.RemoveEmptyEntries)
                .Where(path => Path.GetFileName(path) is var file
                    && (file.StartsWith("System.", StringComparison.Ordinal) || file is "netstandard.dll" or "mscorlib.dll"))
                .Select(path => MetadataReference.CreateFromFile(path)),
            MetadataReference.CreateFromFile(typeof(Money).Assembly.Location),
        ];
    }

    private sealed class Context(IReadOnlyList<InMemoryAssembly> assemblies) : AssemblyLoadContext(isCollectible: true)
    {
        protected override Assembly? Load(AssemblyName assemblyName)
        {
            var match = assemblies.FirstOrDefault(a => a.Name == assemblyName.Name);
            return match == null ? null : LoadFromStream(new MemoryStream(match.Image));
        }
    }
}
