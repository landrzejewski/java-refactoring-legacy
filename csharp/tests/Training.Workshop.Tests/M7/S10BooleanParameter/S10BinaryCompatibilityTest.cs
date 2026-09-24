using System.Collections.Immutable;
using System.Reflection;
using System.Runtime.Loader;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M7.S10BooleanParameter;

/// <summary>
/// Scena s10 w C#: zgodność binarna przy migracji API, sprawdzona na prawdziwych assembly.
/// Roslyn kompiluje w pamięci "bibliotekę" (TicketService) w dwóch wersjach oraz "starego klienta"
/// skompilowanego tylko przeciw wersji 1. Klienta uruchamiamy z wersją 2 podmienioną w
/// AssemblyLoadContext - tak jak po wdrożeniu nowej biblioteki bez przebudowy modułów, które jej używają.
/// </summary>
public sealed class S10BinaryCompatibilityTest
{
    private const string OldClient = """
        public static class OldClient
        {
            public static string Run() => new CineLib.TicketService().Book("Diuna", "IMAX", 2, true, false);
        }
        """;

    [Fact]
    public void ObsoleteOverloadKeepsOldBinariesWorkingUntilSafeDelete()
    {
        var step3 = CompileLibrary(SceneLibrary("Step3"));
        var step4 = CompileLibrary(SceneLibrary("Step4"));
        var client = CompileClient(OldClient, step3);

        Assert.Equal("Diuna IMAX x2 online: 84.00", Run(client, step3));
        Assert.Equal("MissingMethodException", Run(client, step4));
    }

    [Fact]
    public void SafeDeleteIsAlsoASourceBreakForUnmigratedClients()
    {
        var step4 = CompileLibrary(SceneLibrary("Step4"));

        // CS1061: TicketService nie ma już dostępnej metody Book - przebudowa klienta wymaga migracji.
        Assert.Equal(["CS1061"], ErrorsOf(OldClient, step4));
    }

    [Fact]
    public void AddingAnOptionalParameterCompilesButBreaksOldBinaries()
    {
        var v1 = CompileLibrary(MiniLibrary("int seats", "Glasses.Rented"));
        var v2 = CompileLibrary(MiniLibrary("int seats, Glasses glasses = Glasses.Rented", "glasses"));
        const string client = """
            public static class OldClient
            {
                public static string Run() => new CineLib.TicketService().BookOnline("3D", 1);
            }
            """;

        Assert.Equal("3D x1 online: 37.00", Run(CompileClient(client, v1), v1));
        Assert.Equal("MissingMethodException", Run(CompileClient(client, v1), v2));
        Assert.Equal("3D x1 online: 37.00", Run(CompileClient(client, v2), v2));
    }

    [Fact]
    public void DefaultValueIsBakedIntoTheCallerUntilItIsRecompiled()
    {
        var v1 = CompileLibrary(MiniLibrary("int seats, Glasses glasses = Glasses.Rented", "glasses"));
        var v2 = CompileLibrary(MiniLibrary("int seats, Glasses glasses = Glasses.Own", "glasses"));
        const string client = """
            public static class OldClient
            {
                public static string Run() => new CineLib.TicketService().BookOnline("3D", 1);
            }
            """;

        Assert.Equal("3D x1 online: 37.00", Run(CompileClient(client, v1), v2));
        Assert.Equal("3D x1 online: 34.00", Run(CompileClient(client, v2), v2));
    }

    /// <summary>Snapshot sceny jako "biblioteka CineLib" - obie wersje pod tą samą przestrzenią nazw.</summary>
    private static string[] SceneLibrary(string step)
    {
        return
        [
            .. new[] { "TicketService.cs", "Glasses.cs" }.Select(file =>
                File.ReadAllText(SourceFiles.Workshop("M7", "S10BooleanParameter", step, file))
                    .Replace("namespace Training.Workshop.M7.S10BooleanParameter." + step + ";",
                        "namespace CineLib;", StringComparison.Ordinal)),
        ];
    }

    /// <summary>Minimalna biblioteka do pokazania pułapek parametrów opcjonalnych.</summary>
    private static string[] MiniLibrary(string parameters, string glasses)
    {
        return
        [
            $$"""
            namespace CineLib;

            public enum Glasses { Own, Rented }

            public sealed class TicketService
            {
                public string BookOnline(string format, {{parameters}})
                {
                    var glassesUsed = {{glasses}};
                    var total = (format == "3D" ? 32.00m : 25.00m) * seats + 2.00m * seats;
                    if (format == "3D" && glassesUsed == Glasses.Rented)
                    {
                        total += 3.00m * seats;
                    }
                    return format + " x" + seats + " online: "
                        + total.ToString(System.Globalization.CultureInfo.InvariantCulture);
                }
            }
            """,
        ];
    }

    private static byte[] CompileLibrary(string[] sources)
    {
        return Emit("CineLib", [.. sources, "global using System;"], []);
    }

    private static byte[] CompileClient(string source, byte[] library)
    {
        return Emit("OldClient", [source], [MetadataReference.CreateFromImage(library)]);
    }

    private static string[] ErrorsOf(string source, byte[] library)
    {
        var compilation = Compilation("OldClient", [source], [MetadataReference.CreateFromImage(library)]);
        return
        [
            .. compilation.GetDiagnostics()
                .Where(d => d.Severity == DiagnosticSeverity.Error)
                .Select(d => d.Id)
                .Distinct(),
        ];
    }

    private static byte[] Emit(string name, string[] sources, MetadataReference[] references)
    {
        using var image = new MemoryStream();
        var result = Compilation(name, sources, references).Emit(image);
        Assert.True(result.Success, string.Join("\n", result.Diagnostics));
        return image.ToArray();
    }

    private static CSharpCompilation Compilation(string name, string[] sources, MetadataReference[] references)
    {
        return CSharpCompilation.Create(
            name,
            sources.Select(source => CSharpSyntaxTree.ParseText(source, new CSharpParseOptions(LanguageVersion.Latest))),
            [.. PlatformReferences.Value, .. references],
            new CSharpCompilationOptions(OutputKind.DynamicallyLinkedLibrary, nullableContextOptions: NullableContextOptions.Enable));
    }

    /// <summary>Uruchamia skompilowanego klienta z podaną wersją biblioteki; wynik albo nazwa wyjątku.</summary>
    private static string Run(byte[] client, byte[] library)
    {
        var context = new LibraryVersionContext(library);
        try
        {
            var run = context.LoadFromStream(new MemoryStream(client))
                .GetType("OldClient", throwOnError: true)!
                .GetMethod("Run")!;
            return (string)run.Invoke(null, null)!;
        }
        catch (TargetInvocationException e) when (e.InnerException != null)
        {
            return e.InnerException.GetType().Name;
        }
        finally
        {
            context.Unload();
        }
    }

    private static readonly Lazy<ImmutableArray<MetadataReference>> PlatformReferences = new(() =>
    [
        .. ((string)AppContext.GetData("TRUSTED_PLATFORM_ASSEMBLIES")!)
            .Split(Path.PathSeparator, StringSplitOptions.RemoveEmptyEntries)
            .Where(path => Path.GetFileName(path) is var file
                && (file.StartsWith("System.", StringComparison.Ordinal) || file is "mscorlib.dll" or "netstandard.dll"))
            .Select(path => (MetadataReference)MetadataReference.CreateFromFile(path)),
    ]);

    /// <summary>Kontekst ładowania, w którym "CineLib" to wskazana wersja biblioteki.</summary>
    private sealed class LibraryVersionContext(byte[] library) : AssemblyLoadContext(isCollectible: true)
    {
        protected override Assembly? Load(AssemblyName assemblyName)
        {
            return assemblyName.Name == "CineLib" ? LoadFromStream(new MemoryStream(library)) : null;
        }
    }
}
