using System.Collections.Immutable;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;

namespace Training.Workshop.M8.S09Codemod.Step3;

/// <summary>
/// Krok 3: dopasowanie po typach, nie po nazwie. Kod jest analizowany razem ze źródłami projektu
/// (model semantyczny Roslyn), a wywołanie trafia do migracji tylko wtedy, gdy kompilator rozwiązał
/// je do przestarzałej metody Cinema.BookingService.Book. HotelService zostaje w spokoju, a ponowne
/// uruchomienie na zmigrowanym kodzie niczego nie zmienia (idempotencja).
/// </summary>
public sealed class BookCallCodemod
{
    private const string OldApiOwner = "Cinema.BookingService";
    private const string NewTypesNamespace = "Cinema.Options";

    private static readonly Lazy<ImmutableArray<MetadataReference>> References = new(LoadPlatformReferences);

    private sealed record Analyzed(CompilationUnitSyntax Unit, IReadOnlyList<InvocationExpressionSyntax> Calls);

    private readonly IReadOnlyList<string> _projectSources;

    public BookCallCodemod(IReadOnlyList<string> projectSources)
    {
        _projectSources = projectSources.ToList();
    }

    public IReadOnlyList<int> FindLines(string source)
    {
        return Analyze(source).Calls
            .Select(call => call.GetLocation().GetLineSpan().StartLinePosition.Line + 1)
            .ToList();
    }

    public string Rewrite(string source)
    {
        Analyzed parsed = Analyze(source);
        if (parsed.Calls.Count == 0)
        {
            return source;
        }
        var rewriter = new BookCallRewriter(parsed.Calls);
        var unit = (CompilationUnitSyntax)rewriter.Visit(parsed.Unit);
        return WithUsing(unit).ToFullString();
    }

    /// <summary>Dopisuje using dla nowych typów (Channel, Glasses) za ostatnią dyrektywą using, jeśli go brakuje.</summary>
    private static CompilationUnitSyntax WithUsing(CompilationUnitSyntax unit)
    {
        if (unit.Usings.Any(directive => directive.Name?.ToString() == NewTypesNamespace))
        {
            return unit;
        }
        UsingDirectiveSyntax directive = SyntaxFactory.ParseCompilationUnit("using " + NewTypesNamespace + ";\n").Usings[0];
        return unit.AddUsings(directive);
    }

    /// <summary>
    /// Podmienia dokładnie dwa ostatnie argumenty wskazanych wywołań (literał -&gt; stała enum,
    /// wyrażenie -&gt; operator warunkowy), zachowując formatowanie (trivia) reszty pliku.
    /// </summary>
    private sealed class BookCallRewriter(IReadOnlyList<InvocationExpressionSyntax> calls) : CSharpSyntaxRewriter
    {
        public override SyntaxNode? VisitInvocationExpression(InvocationExpressionSyntax node)
        {
            var visited = (InvocationExpressionSyntax)base.VisitInvocationExpression(node)!;
            if (!calls.Contains(node))
            {
                return visited;
            }
            SeparatedSyntaxList<ArgumentSyntax> args = visited.ArgumentList.Arguments;
            args = args.Replace(args[4], Migrate(args[4], "Channel.Web", "Channel.BoxOffice"));
            args = args.Replace(args[5], Migrate(args[5], "Glasses.Own", "Glasses.Rented"));
            return visited.WithArgumentList(visited.ArgumentList.WithArguments(args));
        }

        private static ArgumentSyntax Migrate(ArgumentSyntax arg, string ifTrue, string ifFalse)
        {
            ExpressionSyntax expression = arg.Expression;
            string text;
            if (expression.IsKind(SyntaxKind.TrueLiteralExpression) || expression.IsKind(SyntaxKind.FalseLiteralExpression))
            {
                text = expression.IsKind(SyntaxKind.TrueLiteralExpression) ? ifTrue : ifFalse;
            }
            else
            {
                string original = expression.WithoutTrivia().ToString();
                bool simple = expression is IdentifierNameSyntax or MemberAccessExpressionSyntax
                    or InvocationExpressionSyntax;
                text = (simple ? original : "(" + original + ")") + " ? " + ifTrue + " : " + ifFalse;
            }
            return arg.WithExpression(SyntaxFactory.ParseExpression(text).WithTriviaFrom(expression));
        }
    }

    private Analyzed Analyze(string source)
    {
        SyntaxTree tree = CSharpSyntaxTree.ParseText(source);
        CSharpCompilation compilation = CSharpCompilation.Create(
            "Codemod",
            [.. _projectSources.Select(api => CSharpSyntaxTree.ParseText(api)), tree],
            References.Value,
            new CSharpCompilationOptions(OutputKind.DynamicallyLinkedLibrary));
        SemanticModel model = compilation.GetSemanticModel(tree);
        CompilationUnitSyntax unit = tree.GetCompilationUnitRoot();
        List<InvocationExpressionSyntax> calls = unit.DescendantNodes()
            .OfType<InvocationExpressionSyntax>()
            .Where(call => model.GetSymbolInfo(call).Symbol is IMethodSymbol method && IsOldBook(method))
            .ToList();
        return new Analyzed(unit, calls);
    }

    /// <summary>Typy: przestarzała metoda Book zadeklarowana w Cinema.BookingService.</summary>
    private static bool IsOldBook(IMethodSymbol method)
    {
        return method.Name == "Book"
            && method.GetAttributes().Any(a => a.AttributeClass?.ToDisplayString() == "System.ObsoleteAttribute")
            && method.ContainingType.ToDisplayString() == OldApiOwner;
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
}
