using System.Collections.Immutable;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.Emit;

namespace Training.Module8.Tooling;

/// <summary>
/// Kompiluje kod C# w pamięci przy użyciu Roslyn (odpowiednik
/// <c>InMemoryJavaCompiler</c> opartego na <c>javax.tools</c>).
/// Kod pośredni (IL) trafia wyłącznie do <see cref="MemoryStream"/>.
/// Kontekst nullable jest włączony, więc ostrzeżenia typu CS8602
/// pełnią rolę ostrzeżeń <c>-Xlint:rawtypes</c> z Javy.
/// </summary>
public sealed class InMemoryCSharpCompiler
{
    private static readonly Lazy<ImmutableArray<MetadataReference>> References =
        new(LoadPlatformReferences);

    private static readonly CSharpParseOptions ParseOptions =
        new(LanguageVersion.Latest);

    public CompilationResult Compile(
        string binaryName,
        string source,
        WarningPolicy warningPolicy)
    {
        ValidateBinaryName(binaryName);
        ArgumentNullException.ThrowIfNull(source);
        if (!Enum.IsDefined(warningPolicy))
        {
            throw new ArgumentOutOfRangeException(
                nameof(warningPolicy), warningPolicy, null);
        }
        if (string.IsNullOrWhiteSpace(source))
        {
            throw new ArgumentException("source must not be blank");
        }

        SyntaxTree syntaxTree = CSharpSyntaxTree.ParseText(
            source,
            ParseOptions,
            path: PathFor(binaryName));

        CSharpCompilation compilation = CSharpCompilation.Create(
            assemblyName: binaryName,
            syntaxTrees: [syntaxTree],
            references: References.Value,
            options: CompilerOptions(warningPolicy));

        using var bytecode = new MemoryStream();
        EmitResult emitResult = compilation.Emit(bytecode);

        ImmutableArray<CompilationDiagnostic> stableDiagnostics =
        [
            .. emitResult.Diagnostics
                .Where(diagnostic =>
                    diagnostic.Severity != DiagnosticSeverity.Hidden)
                .Select(ToStableDiagnostic)
        ];

        IEnumerable<string> generatedClassNames = emitResult.Success
            ? DeclaredTypeNames(compilation.Assembly.GlobalNamespace)
            : [];

        return new CompilationResult(
            emitResult.Success,
            stableDiagnostics,
            generatedClassNames);
    }

    private static CSharpCompilationOptions CompilerOptions(
        WarningPolicy warningPolicy) =>
        new(
            OutputKind.DynamicallyLinkedLibrary,
            nullableContextOptions: NullableContextOptions.Enable,
            generalDiagnosticOption: warningPolicy switch
            {
                WarningPolicy.TreatWarningsAsErrors => ReportDiagnostic.Error,
                _ => ReportDiagnostic.Default
            });

    private static CompilationDiagnostic ToStableDiagnostic(
        Diagnostic diagnostic)
    {
        long line = CompilationDiagnostic.NoPosition;
        long column = CompilationDiagnostic.NoPosition;
        if (diagnostic.Location.IsInSource)
        {
            var start = diagnostic.Location.GetLineSpan().StartLinePosition;
            line = start.Line + 1;
            column = start.Character + 1;
        }
        return new CompilationDiagnostic(
            diagnostic.Severity,
            string.IsNullOrWhiteSpace(diagnostic.Id) ? null : diagnostic.Id,
            line,
            column);
    }

    private static IEnumerable<string> DeclaredTypeNames(
        INamespaceSymbol namespaceSymbol)
    {
        foreach (INamespaceSymbol nested in namespaceSymbol.GetNamespaceMembers())
        {
            foreach (string name in DeclaredTypeNames(nested))
            {
                yield return name;
            }
        }
        foreach (INamedTypeSymbol type in namespaceSymbol.GetTypeMembers())
        {
            foreach (string name in TypeNames(type, QualifiedPrefix(namespaceSymbol)))
            {
                yield return name;
            }
        }
    }

    private static IEnumerable<string> TypeNames(
        INamedTypeSymbol type,
        string prefix)
    {
        string name = prefix + type.MetadataName;
        yield return name;
        foreach (INamedTypeSymbol nested in type.GetTypeMembers())
        {
            // Zagnieżdżone typy mają w CLR separator '+' (w JVM '$').
            foreach (string nestedName in TypeNames(nested, name + "+"))
            {
                yield return nestedName;
            }
        }
    }

    private static string QualifiedPrefix(INamespaceSymbol namespaceSymbol) =>
        namespaceSymbol.IsGlobalNamespace
            ? ""
            : namespaceSymbol.ToDisplayString() + ".";

    private static void ValidateBinaryName(string binaryName)
    {
        ArgumentNullException.ThrowIfNull(binaryName);
        bool valid = binaryName.Split('.').All(part =>
            SyntaxFacts.IsValidIdentifier(part)
            && SyntaxFacts.GetKeywordKind(part) == SyntaxKind.None);
        if (!valid)
        {
            throw new ArgumentException(
                "binaryName must be a valid C# type name");
        }
    }

    private static string PathFor(string binaryName) =>
        "memory:///" + binaryName.Replace('.', '/') + ".cs";

    private static ImmutableArray<MetadataReference> LoadPlatformReferences()
    {
        string trustedAssemblies =
            AppContext.GetData("TRUSTED_PLATFORM_ASSEMBLIES") as string
            ?? throw new InvalidOperationException(
                "Platform assemblies are unavailable; run on .NET");

        return
        [
            .. trustedAssemblies
                .Split(Path.PathSeparator, StringSplitOptions.RemoveEmptyEntries)
                .Where(IsFrameworkAssembly)
                .Select(path => MetadataReference.CreateFromFile(path))
        ];
    }

    private static bool IsFrameworkAssembly(string path)
    {
        string fileName = Path.GetFileName(path);
        return fileName.StartsWith("System.", StringComparison.Ordinal)
            || fileName is "System.dll" or "mscorlib.dll" or "netstandard.dll";
    }
}
