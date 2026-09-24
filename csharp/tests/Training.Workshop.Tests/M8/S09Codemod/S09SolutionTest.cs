using System.Collections.Immutable;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Training.Workshop.M8.S09Codemod;

namespace Training.Workshop.Tests.M8.S09Codemod;

/// <summary>Codemod na próbce kodu: co znajduje i jak przepisuje każda wersja; wynik musi się kompilować.</summary>
public sealed class S09SolutionTest
{
    private const string Desk = SampleProject.TicketDesk;

    private const string Migrated = """
        using Cinema;
        using Hotel;
        using Cinema.Options;

        namespace Desk;

        public class TicketDesk
        {
            private readonly BookingService _bookings = new BookingService();
            private readonly HotelService _hotels = new HotelService();

            public string Online(string email, string[] seats, string[] types)
            {
                return _bookings.Book("S1", email, seats, types, Channel.Web, Glasses.Rented);
            }

            public string BoxOffice(string[] seats, string[] types, bool own)
            {
                // stary przyklad: _bookings.Book("S1", "x", seats, types, false, true)
                return _bookings.Book("S2", "kasa@kino.pl",
                    seats, types,
                    Channel.BoxOffice, own ? Glasses.Own : Glasses.Rented);
            }

            public string Stay(string email, string[] rooms, string[] guests)
            {
                return _hotels.Book("H1", email, rooms, guests, true, true);
            }
        }

        """;

    [Fact]
    public void StartRegexHitsCommentAndHotelButMissesMultilineCall()
    {
        var codemod = new Training.Workshop.M8.S09Codemod.Start.BookCallCodemod(SampleProject.Api);
        Assert.Equal([13, 18, 26], codemod.FindLines(Desk));
        string rewritten = codemod.Rewrite(Desk);
        // regex przepisał komentarz
        Assert.Contains("// stary przyklad: _bookings.Book(\"S1\", \"x\", seats, types, "
            + "Channel.BoxOffice, Glasses.Own)", rewritten);
        // wywołanie na kilku liniach zostało nietknięte
        Assert.Contains("false, own);", rewritten);
    }

    [Fact]
    public void Step1AstSearchFindsRealCallsIncludingMultilineOne()
    {
        var codemod = new Training.Workshop.M8.S09Codemod.Step1.BookCallCodemod(SampleProject.Api);
        // 26 = HotelService: składnia nie zna typów
        Assert.Equal([13, 19, 26], codemod.FindLines(Desk));
    }

    [Fact]
    public void Step2SyntacticRewriteBreaksTheHotelCallAndIsNotIdempotent()
    {
        var codemod = new Training.Workshop.M8.S09Codemod.Step2.BookCallCodemod(SampleProject.Api);
        string once = codemod.Rewrite(Desk);
        Assert.Contains("Channel.BoxOffice, own ? Glasses.Own : Glasses.Rented);", once);
        Assert.Contains("_hotels.Book(\"H1\", email, rooms, guests, Channel.Web, Glasses.Own)", once);
        // HotelService nie ma Book(..., Channel, Glasses)
        Assert.NotEmpty(Problems(once));
        // drugie uruchomienie psuje już zmigrowane wywołania
        Assert.NotEqual(once, codemod.Rewrite(once));
    }

    [Fact]
    public void Step3TypeAwareRewriteMigratesOnlyTheDeprecatedApi()
    {
        var codemod = new Training.Workshop.M8.S09Codemod.Step3.BookCallCodemod(SampleProject.Api);
        Assert.Equal([13, 19], codemod.FindLines(Desk));
        Assert.Equal(Migrated, codemod.Rewrite(Desk));
    }

    [Fact]
    public void Step3ResultCompilesWithoutWarningsAndSecondRunChangesNothing()
    {
        var codemod = new Training.Workshop.M8.S09Codemod.Step3.BookCallCodemod(SampleProject.Api);
        Assert.Empty(Problems(Migrated));
        Assert.Equal(Migrated, codemod.Rewrite(Migrated));
        Assert.Empty(codemod.FindLines(Migrated));
    }

    [Fact]
    public void OriginalSampleCompilesButUsesDeprecatedApi()
    {
        Assert.Equal(["WARNING CS0618", "WARNING CS0618"], Problems(Desk));
    }

    /// <summary>Analiza (bez generowania kodu) próbki razem z API, nullable włączone; zwraca błędy i ostrzeżenia.</summary>
    private static IReadOnlyList<string> Problems(string code)
    {
        CSharpCompilation compilation = CSharpCompilation.Create(
            "Sample",
            [.. SampleProject.Api.Select(api => CSharpSyntaxTree.ParseText(api)), CSharpSyntaxTree.ParseText(code)],
            PlatformReferences(),
            new CSharpCompilationOptions(OutputKind.DynamicallyLinkedLibrary,
                nullableContextOptions: NullableContextOptions.Enable));
        return compilation.GetDiagnostics()
            .Where(d => d.Severity is DiagnosticSeverity.Warning or DiagnosticSeverity.Error)
            .Select(d => (d.Severity == DiagnosticSeverity.Error ? "ERROR " : "WARNING ") + d.Id)
            .ToList();
    }

    private static ImmutableArray<MetadataReference> PlatformReferences()
    {
        string trustedAssemblies = (string)AppContext.GetData("TRUSTED_PLATFORM_ASSEMBLIES")!;
        return
        [
            .. trustedAssemblies
                .Split(Path.PathSeparator, StringSplitOptions.RemoveEmptyEntries)
                .Where(path => Path.GetFileName(path).StartsWith("System.", StringComparison.Ordinal)
                    || Path.GetFileName(path) is "mscorlib.dll" or "netstandard.dll")
                .Select(path => MetadataReference.CreateFromFile(path)),
        ];
    }
}
